package codechicken.lib.asm;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

public class RedirectorTransformer implements IClassTransformer {

    private static final boolean DUMP_CLASSES = Boolean.parseBoolean(System.getProperty("ccl.dumpClass", "false"));
    private static final String RenderStateClass = "codechicken/lib/render/CCRenderState";
    private static final Set<String> redirectedFields = new HashSet<>();
    private static final Set<String> redirectedSimpleMethods = new HashSet<>();
    private static final Set<String> redirectedMethods = new HashSet<>();
    private static final ClassConstantPoolParser cstPoolParser;

    static {
        Collections.addAll(
                redirectedFields,
                "pipeline",
                "model",
                "firstVertexIndex",
                "lastVertexIndex",
                "vertexIndex",
                "baseColour",
                "alphaOverride",
                "useNormals",
                "computeLighting",
                "useColour",
                "lightMatrix",
                "vert",
                "hasNormal",
                "normal",
                "hasColour",
                "colour",
                "hasBrightness",
                "brightness",
                "side",
                "lc"

        );
        Collections.addAll(redirectedSimpleMethods, "reset", "pullLightmap", "pushLightmap", "setDynamic", "draw");
        Collections.addAll(
                redirectedMethods,
                "setPipeline",
                "bindModel",
                "setModel",
                "setVertexRange",
                "render",
                "runPipeline",
                "writeVert",
                "setNormal",
                "setColour",
                "setBrightness",
                "startDrawing");

        cstPoolParser = new ClassConstantPoolParser(RenderStateClass);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!cstPoolParser.find(basicClass)) {
            return basicClass;
        }

        final ClassReader cr = new ClassReader(basicClass);
        final ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        boolean changed = false;

        for (MethodNode mn : cn.methods) {
            for (AbstractInsnNode node : mn.instructions.toArray()) {
                if (node instanceof FieldInsnNode fNode) {
                    if (node.getOpcode() == Opcodes.GETSTATIC && redirectedFields.contains(fNode.name)
                            && fNode.owner.equals(RenderStateClass)) {
                        mn.instructions.insertBefore(
                                fNode,
                                new MethodInsnNode(
                                        Opcodes.INVOKESTATIC,
                                        fNode.owner,
                                        "instance",
                                        "()Lcodechicken/lib/render/CCRenderState;"));
                        fNode.setOpcode(Opcodes.GETFIELD);
                        changed = true;
                    } else if (node.getOpcode() == Opcodes.PUTSTATIC
                            && (redirectedFields.contains(fNode.name) && fNode.owner.equals(RenderStateClass))) {
                                InsnList beforePut = new InsnList();
                                beforePut.add(
                                        new MethodInsnNode(
                                                Opcodes.INVOKESTATIC,
                                                fNode.owner,
                                                "instance",
                                                "()Lcodechicken/lib/render/CCRenderState;"));
                                beforePut.add(new InsnNode(Opcodes.SWAP));
                                mn.instructions.insertBefore(fNode, beforePut);
                                fNode.setOpcode(Opcodes.PUTFIELD);
                                changed = true;

                            }
                } else if (node instanceof MethodInsnNode mNode) {
                    if (node.getOpcode() == Opcodes.INVOKESTATIC && redirectedSimpleMethods.contains(mNode.name)
                            && mNode.owner.equals(RenderStateClass)) {
                        mn.instructions.insertBefore(
                                mNode,
                                new MethodInsnNode(
                                        Opcodes.INVOKESTATIC,
                                        mNode.owner,
                                        "instance",
                                        "()Lcodechicken/lib/render/CCRenderState;"));
                        mNode.setOpcode(Opcodes.INVOKEVIRTUAL);
                        mNode.name = mNode.name + "Instance";
                        changed = true;
                    } else if (node.getOpcode() == Opcodes.INVOKEVIRTUAL && mNode.owner.equals(RenderStateClass)
                            && (redirectedSimpleMethods.contains(mNode.name)
                                    || redirectedMethods.contains(mNode.name))) {
                                        // Handle mods that updated to previously new API
                                        mNode.name = mNode.name + "Instance";
                                        changed = true;
                                    }
                }
            }
        }

        if (changed) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            final byte[] bytes = cw.toByteArray();
            if (DUMP_CLASSES) {
                saveTransformedClass(bytes, transformedName);
                saveTransformedClass(basicClass, transformedName + "_original");
            }
            return bytes;
        }
        return basicClass;
    }

    private File outputDir = null;

    private void saveTransformedClass(final byte[] data, final String transformedName) {
        if (!DUMP_CLASSES) {
            return;
        }

        if (outputDir == null) {
            outputDir = new File(Launch.minecraftHome, "ASM_REDIRECTOR");
            try {
                FileUtils.deleteDirectory(outputDir);
            } catch (IOException ignored) {}
            if (!outputDir.exists()) {
                // noinspection ResultOfMethodCallIgnored
                outputDir.mkdirs();
            }
        }

        final String fileName = transformedName.replace('.', File.separatorChar);
        final File classFile = new File(outputDir, fileName + ".class");
        final File bytecodeFile = new File(outputDir, fileName + "_BYTE.txt");
        final File asmifiedFile = new File(outputDir, fileName + "_ASM.txt");
        final File outDir = classFile.getParentFile();
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        if (classFile.exists()) {
            classFile.delete();
        }
        try (final OutputStream output = Files.newOutputStream(classFile.toPath())) {
            output.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bytecodeFile.exists()) {
            bytecodeFile.delete();
        }
        try (final OutputStream output = Files.newOutputStream(bytecodeFile.toPath())) {
            final ClassReader classReader = new ClassReader(data);
            classReader.accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(output)), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (asmifiedFile.exists()) {
            asmifiedFile.delete();
        }
        try (final OutputStream output = Files.newOutputStream(asmifiedFile.toPath())) {
            final ClassReader classReader = new ClassReader(data);
            classReader.accept(new TraceClassVisitor(null, new ASMifier(), new PrintWriter(output)), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

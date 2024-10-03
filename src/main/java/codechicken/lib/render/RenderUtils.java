package codechicken.lib.render;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.ENTITY;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Rectangle4i;
import codechicken.lib.vec.Vector3;

public class RenderUtils {

    static Vector3[] vectors;
    static RenderItem uniformRenderItem;
    static EntityItem entityItem;

    static {
        vectors = new Vector3[8];
        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = new Vector3();
        }
        uniformRenderItem = new RenderItem() {

            public boolean shouldBob() {
                return false;
            }
        };
        uniformRenderItem.setRenderManager(RenderManager.instance);
        entityItem = new EntityItem(null);
        entityItem.hoverStart = 0;
    }

    public static void renderFluidQuad(Vector3 point1, Vector3 point2, Vector3 point3, Vector3 point4, IIcon icon,
            double res) {
        renderFluidQuad(
                point2,
                vectors[0].set(point4).subtract(point1),
                vectors[1].set(point1).subtract(point2),
                icon,
                res);
    }

    /**
     * Draws a tessellated quadrilateral bottom to top, left to right
     *
     * @param base The bottom left corner of the quad
     * @param wide The bottom of the quad
     * @param high The left side of the quad
     * @param res  Units per icon
     */
    public static void renderFluidQuad(Vector3 base, Vector3 wide, Vector3 high, IIcon icon, double res) {
        Tessellator tessellator = Tessellator.instance;

        double u1 = icon.getMinU();
        double du = icon.getMaxU() - icon.getMinU();
        double v2 = icon.getMaxV();
        double dv = icon.getMaxV() - icon.getMinV();

        double wlen = wide.mag();
        double hlen = high.mag();

        double x = 0;
        while (x < wlen) {
            double rx = wlen - x;
            if (rx > res) rx = res;

            double y = 0;
            while (y < hlen) {
                double ry = hlen - y;
                if (ry > res) ry = res;

                Vector3 dx1 = vectors[2].set(wide).multiply(x / wlen);
                Vector3 dx2 = vectors[3].set(wide).multiply((x + rx) / wlen);
                Vector3 dy1 = vectors[4].set(high).multiply(y / hlen);
                Vector3 dy2 = vectors[5].set(high).multiply((y + ry) / hlen);

                tessellator.addVertexWithUV(
                        base.x + dx1.x + dy2.x,
                        base.y + dx1.y + dy2.y,
                        base.z + dx1.z + dy2.z,
                        u1,
                        v2 - ry / res * dv);
                tessellator.addVertexWithUV(
                        base.x + dx1.x + dy1.x,
                        base.y + dx1.y + dy1.y,
                        base.z + dx1.z + dy1.z,
                        u1,
                        v2);
                tessellator.addVertexWithUV(
                        base.x + dx2.x + dy1.x,
                        base.y + dx2.y + dy1.y,
                        base.z + dx2.z + dy1.z,
                        u1 + rx / res * du,
                        v2);
                tessellator.addVertexWithUV(
                        base.x + dx2.x + dy2.x,
                        base.y + dx2.y + dy2.y,
                        base.z + dx2.z + dy2.z,
                        u1 + rx / res * du,
                        v2 - ry / res * dv);

                y += ry;
            }

            x += rx;
        }
    }

    public static void translateToWorldCoords(Entity entity, float partialTicks) {
        double interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        GL11.glTranslated(-interpPosX, -interpPosY, -interpPosZ);
    }

    public static void drawCuboidOutline(Cuboid6 c) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawing(3);
        tess.addVertex(c.min.x, c.min.y, c.min.z);
        tess.addVertex(c.max.x, c.min.y, c.min.z);
        tess.addVertex(c.max.x, c.min.y, c.max.z);
        tess.addVertex(c.min.x, c.min.y, c.max.z);
        tess.addVertex(c.min.x, c.min.y, c.min.z);
        tess.draw();
        tess.startDrawing(3);
        tess.addVertex(c.min.x, c.max.y, c.min.z);
        tess.addVertex(c.max.x, c.max.y, c.min.z);
        tess.addVertex(c.max.x, c.max.y, c.max.z);
        tess.addVertex(c.min.x, c.max.y, c.max.z);
        tess.addVertex(c.min.x, c.max.y, c.min.z);
        tess.draw();
        tess.startDrawing(1);
        tess.addVertex(c.min.x, c.min.y, c.min.z);
        tess.addVertex(c.min.x, c.max.y, c.min.z);
        tess.addVertex(c.max.x, c.min.y, c.min.z);
        tess.addVertex(c.max.x, c.max.y, c.min.z);
        tess.addVertex(c.max.x, c.min.y, c.max.z);
        tess.addVertex(c.max.x, c.max.y, c.max.z);
        tess.addVertex(c.min.x, c.min.y, c.max.z);
        tess.addVertex(c.min.x, c.max.y, c.max.z);
        tess.draw();
    }

    public static void renderFluidCuboid(CCRenderState state, Cuboid6 bound, IIcon tex, double res) {
        renderFluidCuboid(bound, tex, res);
    }

    public static void renderFluidCuboid(Cuboid6 bound, IIcon tex, double res) {
        final double minX = bound.min.x;
        final double minY = bound.min.y;
        final double minZ = bound.min.z;
        final double maxX = bound.max.x;
        final double maxY = bound.max.y;
        final double maxZ = bound.max.z;
        renderFluidQuad( // bottom
                new Vector3(minX, minY, minZ),
                new Vector3(maxX, minY, minZ),
                null,
                new Vector3(minX, minY, maxZ),
                tex,
                res);
        renderFluidQuad( // top
                new Vector3(minX, maxY, minZ),
                new Vector3(minX, maxY, maxZ),
                null,
                new Vector3(maxX, maxY, minZ),
                tex,
                res);
        renderFluidQuad( // -x
                new Vector3(minX, maxY, minZ),
                new Vector3(minX, minY, minZ),
                null,
                new Vector3(minX, maxY, maxZ),
                tex,
                res);
        renderFluidQuad( // +x
                new Vector3(maxX, maxY, maxZ),
                new Vector3(maxX, minY, maxZ),
                null,
                new Vector3(maxX, maxY, minZ),
                tex,
                res);
        renderFluidQuad( // -z
                new Vector3(maxX, maxY, minZ),
                new Vector3(maxX, minY, minZ),
                null,
                new Vector3(minX, maxY, minZ),
                tex,
                res);
        renderFluidQuad( // +z
                new Vector3(minX, maxY, maxZ),
                new Vector3(minX, minY, maxZ),
                null,
                new Vector3(maxX, maxY, maxZ),
                tex,
                res);
    }

    public static void renderBlockOverlaySide(int x, int y, int z, int side, double tx1, double tx2, double ty1,
            double ty2) {
        Tessellator tessellator = Tessellator.instance;
        final double minX = x - 0.009;
        final double maxX = x + 1.009;
        final double minY = y - 0.009;
        final double maxY = y + 1.009;
        final double minZ = z - 0.009;
        final double maxZ = z + 1.009;
        switch (side) {
            case 0:
                tessellator.addVertexWithUV(minX, minY, minZ, tx1, ty1);
                tessellator.addVertexWithUV(maxX, minY, minZ, tx2, ty1);
                tessellator.addVertexWithUV(maxX, minY, maxZ, tx2, ty2);
                tessellator.addVertexWithUV(minX, minY, maxZ, tx1, ty2);
                break;
            case 1:
                tessellator.addVertexWithUV(maxX, maxY, minZ, tx2, ty1);
                tessellator.addVertexWithUV(minX, maxY, minZ, tx1, ty1);
                tessellator.addVertexWithUV(minX, maxY, maxZ, tx1, ty2);
                tessellator.addVertexWithUV(maxX, maxY, maxZ, tx2, ty2);
                break;
            case 2:
                tessellator.addVertexWithUV(minX, maxY, minZ, tx2, ty1);
                tessellator.addVertexWithUV(maxX, maxY, minZ, tx1, ty1);
                tessellator.addVertexWithUV(maxX, minY, minZ, tx1, ty2);
                tessellator.addVertexWithUV(minX, minY, minZ, tx2, ty2);
                break;
            case 3:
                tessellator.addVertexWithUV(maxX, maxY, maxZ, tx2, ty1);
                tessellator.addVertexWithUV(minX, maxY, maxZ, tx1, ty1);
                tessellator.addVertexWithUV(minX, minY, maxZ, tx1, ty2);
                tessellator.addVertexWithUV(maxX, minY, maxZ, tx2, ty2);
                break;
            case 4:
                tessellator.addVertexWithUV(minX, maxY, maxZ, tx2, ty1);
                tessellator.addVertexWithUV(minX, maxY, minZ, tx1, ty1);
                tessellator.addVertexWithUV(minX, minY, minZ, tx1, ty2);
                tessellator.addVertexWithUV(minX, minY, maxZ, tx2, ty2);
                break;
            case 5:
                tessellator.addVertexWithUV(maxX, maxY, minZ, tx2, ty1);
                tessellator.addVertexWithUV(maxX, maxY, maxZ, tx1, ty1);
                tessellator.addVertexWithUV(maxX, minY, maxZ, tx1, ty2);
                tessellator.addVertexWithUV(maxX, minY, minZ, tx2, ty2);
                break;
        }
    }

    public static boolean shouldRenderFluid(FluidStack stack) {
        return stack.amount > 0 && stack.getFluid() != null;
    }

    /**
     * @param stack The fluid stack to render
     * @return The icon of the fluid
     */
    public static IIcon prepareFluidRender(CCRenderState state, FluidStack stack, int alpha) {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Fluid fluid = stack.getFluid();
        state.setColourInstance(fluid.getColor(stack) << 8 | alpha);
        TextureUtils.bindAtlas(fluid.getSpriteNumber());
        return TextureUtils.safeIcon(fluid.getIcon(stack));
    }

    public static IIcon prepareFluidRender(FluidStack stack, int alpha) {
        return prepareFluidRender(CCRenderState.instance(), stack, alpha);
    }

    /**
     * Re-enables lighting and disables blending.
     */
    public static void postFluidRender() {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static double fluidDensityToAlpha(double d) {
        return Math.pow(d, 0.4);
    }

    /**
     * Renders a fluid within a bounding box. If the fluid is a liquid it will render as a normal tank with height equal
     * to fillRate/bound.height. If the fluid is a gas, it will render the full box with an alpha equal to fillRate.
     * Warning, bound will be mutated if the fluid is a liquid
     *
     * @param stack    The fluid to render.
     * @param bound    The bounding box within which the fluid is contained.
     * @param fillRate The volume of fluid / the capacity of the tank. This is a double between 0 and 1.
     * @param res      The resolution to render at.
     */
    public static void renderFluidCuboid(CCRenderState state, FluidStack stack, Cuboid6 bound, double fillRate,
            double res) {
        if (!shouldRenderFluid(stack)) return;

        fillRate = MathHelper.clamp_double(fillRate, 0d, 1d);
        int alpha = 255;
        if (stack.getFluid().isGaseous()) {
            alpha = (int) (fluidDensityToAlpha(fillRate) * 255);
        } else {
            bound.max.y = bound.min.y + (bound.max.y - bound.min.y) * fillRate;
        }

        IIcon tex = prepareFluidRender(state, stack, alpha);
        state.startDrawingInstance();
        renderFluidCuboid(bound, tex, res);
        state.drawInstance();
        postFluidRender();
    }

    /**
     * Renders a fluid within a bounding box. If the fluid is a liquid it will render as a normal tank with height equal
     * to fillRate/bound.height. If the fluid is a gas, it will render the full box with an alpha equal to fillRate.
     * Warning, bound will be mutated if the fluid is a liquid
     *
     * @param stack    The fluid to render.
     * @param bound    The bounding box within which the fluid is contained.
     * @param fillRate The volume of fluid / the capacity of the tank. This is a double between 0 and 1.
     * @param res      The resolution to render at.
     */
    public static void renderFluidCuboid(FluidStack stack, Cuboid6 bound, double fillRate, double res) {
        renderFluidCuboid(CCRenderState.instance(), stack, bound, fillRate, res);
    }

    public static void renderFluidGauge(CCRenderState state, FluidStack stack, Rectangle4i rect, double fillRate,
            double res) {
        if (!shouldRenderFluid(stack)) return;

        fillRate = MathHelper.clamp_double(fillRate, 0d, 1d);
        int alpha = 255;
        if (stack.getFluid().isGaseous()) {
            alpha = (int) (fluidDensityToAlpha(fillRate) * 255);
        } else {
            int height = (int) (rect.h * fillRate);
            rect.y += rect.h - height;
            rect.h = height;
        }

        IIcon tex = prepareFluidRender(state, stack, alpha);
        state.startDrawingInstance();
        renderFluidQuad(
                new Vector3(rect.x, rect.y + rect.h, 0),
                new Vector3(rect.w, 0, 0),
                new Vector3(0, -rect.h, 0),
                tex,
                res);
        state.drawInstance();
        postFluidRender();
    }

    public static void renderFluidGauge(FluidStack stack, Rectangle4i rect, double fillRate, double res) {
        renderFluidGauge(CCRenderState.instance(), stack, rect, fillRate, res);
    }

    /**
     * Renders items and blocks in the world at 0,0,0 with transformations that size them appropriately
     */
    public static void renderItemUniform(ItemStack item) {
        renderItemUniform(item, 0);
    }

    /**
     * Renders items and blocks in the world at 0,0,0 with transformations that size them appropriately
     *
     * @param spin The spin angle of the item around the y axis in degrees
     */
    public static void renderItemUniform(ItemStack item, double spin) {
        IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(item, ENTITY);
        boolean is3D = customRenderer != null && customRenderer.shouldUseRenderHelper(ENTITY, item, BLOCK_3D);

        boolean larger = false;
        if (item.getItem() instanceof ItemBlock
                && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(item.getItem()).getRenderType())) {
            int renderType = Block.getBlockFromItem(item.getItem()).getRenderType();
            larger = !(renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2);
        } else if (is3D) {
            larger = true;
        }

        double d = 2;
        double d1 = 1 / d;
        if (larger) GL11.glScaled(d, d, d);

        GL11.glColor4f(1, 1, 1, 1);

        entityItem.setEntityItemStack(item);
        uniformRenderItem.doRender(entityItem, 0, larger ? 0.09 : 0.06, 0, 0, (float) (spin * 9 / Math.PI));

        if (larger) GL11.glScaled(d1, d1, d1);
    }
}

package codechicken.core.launch;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

/**
 * This was used to download dependencies during runtime and inject them into the classloader. It is now hollowed out
 * but kept in the mod so that if another mod copied-pasted this class into their mod, there is a chance this empty
 * class will be loaded instead and it will do nothing.
 */
@MCVersion("1.7.10")
public class DepLoader implements IFMLLoadingPlugin, IFMLCallHook {

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public Void call() {
        return null;
    }
}

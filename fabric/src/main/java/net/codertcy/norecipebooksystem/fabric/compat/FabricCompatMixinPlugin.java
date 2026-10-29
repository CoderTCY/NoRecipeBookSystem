package net.codertcy.norecipebooksystem.fabric.compat;

import net.codertcy.norecipebooksystem.common.compat.CompatMixinPlugin;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public final class FabricCompatMixinPlugin extends CompatMixinPlugin {
    @Override
    protected boolean isActiveLoader() {
        return getClass().getClassLoader().getResource("net/fabricmc/loader/api/FabricLoader.class") != null;
    }

    @Override
    protected boolean isClient() { return Detection.isClient(); }
    @Override
    protected boolean isModLoaded(String modId) { return Detection.isModLoaded(modId); }

    // Kept separate so a universal jar never links another loader's APIs.
    private static final class Detection {
        static boolean isClient() { return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT; }
        static boolean isModLoaded(String modId) { return FabricLoader.getInstance().isModLoaded(modId); }
    }
}

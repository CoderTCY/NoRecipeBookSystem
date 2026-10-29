package net.codertcy.norecipebooksystem.neoforge.compat;

import net.codertcy.norecipebooksystem.common.compat.CompatMixinPlugin;
import net.neoforged.fml.loading.FMLLoader;
import java.util.Objects;

public final class NeoForgeCompatMixinPlugin extends CompatMixinPlugin {
    @Override
    protected boolean isActiveLoader() {
        return getClass().getClassLoader().getResource("net/neoforged/fml/loading/FMLLoader.class") != null;
    }

    @Override
    protected boolean isClient() { return Detection.isClient(); }
    @Override
    protected boolean isModLoaded(String modId) { return Detection.isModLoaded(modId); }

    // Kept separate so a universal jar never links another loader's APIs.
    private static final class Detection {
        static boolean isClient() { return FMLLoader.getDist().isClient(); }
        static boolean isModLoaded(String modId) {
            return Objects.requireNonNull(FMLLoader.getLoadingModList(), "Mod discovery has not completed")
                    .getModFileById(modId) != null;
        }
    }
}

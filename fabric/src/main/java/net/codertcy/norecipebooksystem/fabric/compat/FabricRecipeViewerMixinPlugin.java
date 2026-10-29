package net.codertcy.norecipebooksystem.fabric.compat;

import net.codertcy.norecipebooksystem.common.compat.RecipeViewerMixinPlugin;
import net.codertcy.norecipebooksystem.common.compat.RecipeViewerMods;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public final class FabricRecipeViewerMixinPlugin extends RecipeViewerMixinPlugin {
    @Override
    protected RecipeViewerMods detectMods() {
        FabricLoader loader = FabricLoader.getInstance();
        boolean client = loader.getEnvironmentType() == EnvType.CLIENT;
        return new RecipeViewerMods(
                client && loader.isModLoaded("jei"),
                client && loader.isModLoaded("rrv"),
                client && loader.isModLoaded("roughlyenoughitems"));
    }
}

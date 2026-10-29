package net.codertcy.norecipebooksystem.neoforge.compat;

import net.codertcy.norecipebooksystem.common.compat.RecipeViewerMixinPlugin;
import net.codertcy.norecipebooksystem.common.compat.RecipeViewerMods;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.LoadingModList;

import java.util.Objects;

public final class NeoForgeRecipeViewerMixinPlugin extends RecipeViewerMixinPlugin {
    @Override
    protected RecipeViewerMods detectMods() {
        FMLLoader loader = FMLLoader.getCurrent();
        if (!loader.getDist().isClient()) return new RecipeViewerMods(false, false, false, false);
        // Mixin configs are initialized before ModList; use the completed discovery list instead.
        LoadingModList mods = Objects.requireNonNull(loader.getLoadingModList(), "NeoForge mod discovery is not complete");
        return new RecipeViewerMods(
                mods.getModFileById("jei") != null,
                mods.getModFileById("rrv") != null,
                mods.getModFileById("roughlyenoughitems") != null,
                mods.getModFileById("eiv") != null);
    }
}

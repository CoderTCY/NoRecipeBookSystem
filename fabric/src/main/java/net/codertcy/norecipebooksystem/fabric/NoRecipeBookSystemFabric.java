package net.codertcy.norecipebooksystem.fabric;

import net.codertcy.norecipebooksystem.common.NoRecipeBookSystem;
import net.fabricmc.api.ModInitializer;

/**
 * Fabric entry point for NoRecipeBookSystem.
 *
 * <p>Registered in {@code fabric.mod.json} as the mod initializer. Delegates
 * to {@link NoRecipeBookSystem#init()} during mod initialisation.
 */
public class NoRecipeBookSystemFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NoRecipeBookSystem.init();
    }
}

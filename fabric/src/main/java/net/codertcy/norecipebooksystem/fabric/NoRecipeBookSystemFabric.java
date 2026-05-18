package net.codertcy.norecipebooksystem.fabric;

import net.codertcy.norecipebooksystem.common.NoRecipeBookSystem;
import net.fabricmc.api.ModInitializer;

public class NoRecipeBookSystemFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NoRecipeBookSystem.init();
    }
}

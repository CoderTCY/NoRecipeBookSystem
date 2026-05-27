package net.codertcy.norecipebooksystem.forge;

import net.codertcy.norecipebooksystem.common.NoRecipeBookSystem;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge entry point for NoRecipeBookSystem.
 *
 * <p>Registered in {@code mods.toml} via the {@link Mod} annotation. The
 * constructor is called by the Forge mod loading framework and delegates to
 * {@link NoRecipeBookSystem#init()}.
 */
@Mod(NoRecipeBookSystem.MODID)
public class NoRecipeBookSystemForge {
    /**
     * Called by the Forge mod loading framework to initialise the mod.
     * Delegates to {@link NoRecipeBookSystem#init()}.
     */
    public NoRecipeBookSystemForge() {
        NoRecipeBookSystem.init();
    }
}

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
     * 由 Forge 模组加载框架调用以初始化模组。
     * 委托给 {@link NoRecipeBookSystem#init()}。
     */
    public NoRecipeBookSystemForge() {
        NoRecipeBookSystem.init();
    }
}

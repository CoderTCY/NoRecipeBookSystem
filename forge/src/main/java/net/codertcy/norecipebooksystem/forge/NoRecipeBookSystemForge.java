package net.codertcy.norecipebooksystem.forge;

import net.codertcy.norecipebooksystem.common.NoRecipeBookSystem;
import net.minecraftforge.fml.common.Mod;

@Mod(NoRecipeBookSystem.MODID)
public class NoRecipeBookSystemForge {
    public NoRecipeBookSystemForge() {
        NoRecipeBookSystem.init();
    }
}

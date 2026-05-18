package net.codertcy.norecipebooksystem.neoforge;

import net.codertcy.norecipebooksystem.common.NoRecipeBookSystem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(NoRecipeBookSystem.MODID)
public class NoRecipeBookSystemNeoForge {
    public NoRecipeBookSystemNeoForge(IEventBus modBus) {
        NoRecipeBookSystem.init();
    }
}

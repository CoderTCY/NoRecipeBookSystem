package net.codertcy.norecipebooksystem.common.mixin.compat;

import me.shedaniel.rei.api.client.favorites.FavoriteMenuEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(targets = "me.shedaniel.rei.impl.client.gui.modules.entries.SubMenuEntry", remap = false)
public interface REISubMenuEntryAccessor {
    @Accessor("entries")
    List<FavoriteMenuEntry> norecipebooksystem$getEntries();

    @Accessor("entries")
    void norecipebooksystem$setEntries(List<FavoriteMenuEntry> entries);
}

package net.codertcy.norecipebooksystem.common.mixin.compact;

import me.shedaniel.rei.api.client.favorites.FavoriteMenuEntry;
import me.shedaniel.rei.impl.client.gui.modules.entries.SubMenuEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = SubMenuEntry.class, remap = false)
public interface REISubMenuEntryAccessor {
    @Accessor("entries")
    List<FavoriteMenuEntry> norecipebooksystem$getEntries();

    @Accessor("entries")
    void norecipebooksystem$setEntries(List<FavoriteMenuEntry> entries);
}

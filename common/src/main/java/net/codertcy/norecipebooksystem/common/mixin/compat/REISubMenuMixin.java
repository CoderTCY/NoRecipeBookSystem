package net.codertcy.norecipebooksystem.common.mixin.compat;

import me.shedaniel.rei.api.client.favorites.FavoriteMenuEntry;
import net.codertcy.norecipebooksystem.common.compat.ReiConfigCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

/** Removes REI's recipe book toggle from the Display Settings quick menu. */
@Mixin(targets = "me.shedaniel.rei.impl.client.gui.widget.ConfigButtonWidget", priority = 2000, remap = false)
public abstract class REISubMenuMixin {
    @Inject(method = "menuEntries", at = @At("RETURN"))
    private static void removeRecipeBookFromSubMenu(CallbackInfoReturnable<Collection<FavoriteMenuEntry>> cir) {
        ReiConfigCompat.filterMenu(cir.getReturnValue());
    }
}

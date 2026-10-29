package net.codertcy.norecipebooksystem.common.mixin.compact;

import me.shedaniel.rei.api.client.favorites.FavoriteMenuEntry;
import net.codertcy.norecipebooksystem.common.compat.ReiCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

/**
 * Removes the "Remove Recipe Book" toggle from the "Display Settings" submenu
 * in REI's gear icon quick-menu.
 *
 * <p>{@code ConfigButtonWidget.menuEntries()} builds the popup menu as a list
 * of {@code FavoriteMenuEntry} objects, one of which is a {@code SubMenuEntry}
 * titled "Display Settings…". Inside that submenu is a {@code ToggleMenuEntry}
 * for "Remove Recipe Book" backed by {@code config::doesDisableRecipeBook}.
 *
 * <p>This mixin intercepts the return value of {@code menuEntries()}, locates the
 * {@code SubMenuEntry} with translation key {@code text.rei.config.menu.display},
 * copies its {@code entries} list, filters out the toggle whose component key
 * is {@code text.rei.config.menu.display.remove_recipe_book}, and writes the
 * filtered list back through a Mixin accessor.
 *
 * <p>REI is optional — this mixin only activates when REI is present.
 *
 * @see REIConfigObjectMixin Overrides the getter at the API level
 * @see REIConfigScreenMixin  Removes the option from the config screen UI
 */
@Mixin(targets = "me.shedaniel.rei.impl.client.gui.widget.ConfigButtonWidget", priority = 2000, remap = false)
public abstract class REISubMenuMixin {
    /** 禁止实例化；此类仅供 Mixin 注入使用。 */
    private REISubMenuMixin() {}

    @Inject(method = "menuEntries", at = @At("RETURN"), remap = false, require = 0)
    private static void removeRecipeBookFromSubMenu(CallbackInfoReturnable<Collection<FavoriteMenuEntry>> cir) {
        ReiCompat.removeRecipeBookToggle(cir.getReturnValue());
    }
}

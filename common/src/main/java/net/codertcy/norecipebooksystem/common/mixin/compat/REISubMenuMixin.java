package net.codertcy.norecipebooksystem.common.mixin.compat;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
 * filtered list back via reflection.
 *
 * <p>REI is optional — this mixin only activates when REI is present.
 *
 * @see REIConfigObjectMixin Overrides the getter at the API level
 * @see REIConfigScreenMixin  Removes the option from the main config screen UI
 */
@Pseudo
@Mixin(targets = "me.shedaniel.rei.impl.client.gui.widget.ConfigButtonWidget", priority = 2000)
public abstract class REISubMenuMixin {
    /** 禁止实例化；此类仅供 Mixin 注入使用。 */
    private REISubMenuMixin() {}

    @Unique
    private static final String DISPLAY_KEY = "text.rei.config.menu.display";

    @Unique
    private static final String REMOVE_RECIPE_BOOK_KEY = "text.rei.config.menu.display.remove_recipe_book";

    @Inject(method = "menuEntries", at = @At("RETURN"), remap = false)
    private static void removeRecipeBookFromSubMenu(CallbackInfoReturnable<Collection<?>> cir) {
        Collection<?> original = cir.getReturnValue();
        if (original == null || original.isEmpty()) return;

        try {
            Class<?> subMenuEntryClass = Class.forName(
                    "me.shedaniel.rei.impl.client.gui.modules.entries.SubMenuEntry");
            Class<?> toggleMenuEntryClass = Class.forName(
                    "me.shedaniel.rei.impl.client.gui.modules.entries.ToggleMenuEntry");

            Field subMenuTextField = subMenuEntryClass.getField("text");
            Field subMenuEntriesField = subMenuEntryClass.getDeclaredField("entries");
            subMenuEntriesField.setAccessible(true);

            Field toggleTextField = toggleMenuEntryClass.getField("text");

            for (Object entry : original) {
                if (!subMenuEntryClass.isInstance(entry)) continue;

                Component text = (Component) subMenuTextField.get(entry);
                if (!hasTranslationKey(text, DISPLAY_KEY)) continue;

                @SuppressWarnings("unchecked")
                List<Object> entries = (List<Object>) subMenuEntriesField.get(entry);
                List<Object> filtered = new ArrayList<>(entries);
                filtered.removeIf(e -> {
                    if (e == null || !toggleMenuEntryClass.isInstance(e)) return false;
                    try {
                        Component t = (Component) toggleTextField.get(e);
                        return hasTranslationKey(t, REMOVE_RECIPE_BOOK_KEY);
                    } catch (Exception ex) {
                        return false;
                    }
                });
                subMenuEntriesField.set(entry, filtered);
            }
        } catch (Exception ignored) {
            // 反射静默失败——REI 可能不存在或内部结构已变更
        }
    }

    /**
     * 检查 {@link Component} 的翻译键是否与给定键匹配。
     * 若无法获取翻译键，则回退到对渲染文本的字符串比较。
     */
    @Unique
    private static boolean hasTranslationKey(Component component, String key) {
        if (component == null) return false;
        if (component instanceof MutableComponent mutable) {
            if (mutable.getContents() instanceof TranslatableContents translatable) {
                return key.equals(translatable.getKey());
            }
        }
        // 回退：与渲染后的字符串进行比较
        return key.equals(component.getString());
    }
}

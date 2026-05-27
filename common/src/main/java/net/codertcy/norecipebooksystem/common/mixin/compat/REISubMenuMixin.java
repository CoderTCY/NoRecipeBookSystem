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
 * 从 REI 齿轮图标的快捷菜单中，移除 Display Settings SubMenu
 * 里的 "Remove Recipe Book" 开关项，使用户无法在此入口切换。
 * REI 是可选依赖，仅在 REI 加载时生效。
 */
@Pseudo
@Mixin(targets = "me.shedaniel.rei.impl.client.gui.widget.ConfigButtonWidget", priority = 2000)
public abstract class REISubMenuMixin {

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
            // REI 不存在或结构变化时静默跳过
        }
    }

    @Unique
    private static boolean hasTranslationKey(Component component, String key) {
        if (component == null) return false;
        // 检查 Component 是否为可翻译文本且 key 匹配
        if (component instanceof MutableComponent mutable) {
            if (mutable.getContents() instanceof TranslatableContents translatable) {
                return key.equals(translatable.getKey());
            }
        }
        // 回退：比对渲染后的字符串（兜底）
        return key.equals(component.getString());
    }
}

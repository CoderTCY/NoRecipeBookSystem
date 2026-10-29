package net.codertcy.norecipebooksystem.common.compat;

import me.shedaniel.rei.api.client.favorites.FavoriteMenuEntry;
import me.shedaniel.rei.impl.client.gui.config.options.CompositeOption;
import me.shedaniel.rei.impl.client.gui.config.options.OptionCategory;
import me.shedaniel.rei.impl.client.gui.config.options.OptionGroup;
import me.shedaniel.rei.impl.client.gui.modules.entries.SubMenuEntry;
import me.shedaniel.rei.impl.client.gui.modules.entries.ToggleMenuEntry;
import net.codertcy.norecipebooksystem.common.mixin.compat.REISubMenuEntryAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Loaded only by the optional REI client mixins. */
public final class ReiConfigCompat {
    private ReiConfigCompat() {}

    public static List<OptionCategory> filterCategories(List<OptionCategory> categories) {
        if (categories == null) return null;
        for (OptionCategory category : categories) {
            if (category == null) continue;
            List<OptionGroup> groups = category.getGroups();
            if (groups == null) continue;
            for (OptionGroup group : groups) {
                if (group == null) continue;
                List<CompositeOption<?>> options = group.getOptions();
                if (options == null) continue;
                options.removeIf(option -> option != null
                        && "accessibility.vanilla_recipe_book".equals(option.getId()));
            }
        }
        return categories;
    }

    public static void filterMenu(Collection<FavoriteMenuEntry> original) {
        if (original == null || original.isEmpty()) return;
        for (FavoriteMenuEntry entry : original) {
            if (!(entry instanceof SubMenuEntry submenu)
                    || !hasTranslationKey(submenu.text, "text.rei.config.menu.display")) continue;
            REISubMenuEntryAccessor accessor = (REISubMenuEntryAccessor) submenu;
            List<FavoriteMenuEntry> filtered = new ArrayList<>(accessor.norecipebooksystem$getEntries());
            filtered.removeIf(child -> child instanceof ToggleMenuEntry toggle
                    && hasTranslationKey(toggle.text, "text.rei.config.menu.display.remove_recipe_book"));
            accessor.norecipebooksystem$setEntries(filtered);
        }
    }

    private static boolean hasTranslationKey(Component component, String key) {
        if (component == null) return false;
        if (component instanceof MutableComponent mutable
                && mutable.getContents() instanceof TranslatableContents translatable) {
            return key.equals(translatable.getKey());
        }
        return key.equals(component.getString());
    }
}

package net.codertcy.norecipebooksystem.common.compat;

import me.shedaniel.rei.api.client.favorites.FavoriteMenuEntry;
import me.shedaniel.rei.impl.client.gui.config.options.CompositeOption;
import me.shedaniel.rei.impl.client.gui.config.options.OptionCategory;
import me.shedaniel.rei.impl.client.gui.config.options.OptionGroup;
import me.shedaniel.rei.impl.client.gui.modules.entries.SubMenuEntry;
import me.shedaniel.rei.impl.client.gui.modules.entries.ToggleMenuEntry;
import net.codertcy.norecipebooksystem.common.mixin.compact.REISubMenuEntryAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Only called by REI mixins after the loader has confirmed a client installation. */
public final class ReiCompat {
    private static final String TARGET_OPTION_ID = "accessibility.vanilla_recipe_book";
    private static final String DISPLAY_KEY = "text.rei.config.menu.display";
    private static final String REMOVE_RECIPE_BOOK_KEY = "text.rei.config.menu.display.remove_recipe_book";

    private ReiCompat() {}

    public static List<OptionCategory> removeRecipeBookOption(List<OptionCategory> categories) {
        if (categories == null) return null;
        for (OptionCategory category : categories) {
            if (category == null) continue;
            List<OptionGroup> groups = category.getGroups();
            if (groups == null) continue;
            for (OptionGroup group : groups) {
                if (group == null) continue;
                List<CompositeOption<?>> options = group.getOptions();
                if (options != null) {
                    options.removeIf(option -> option != null && TARGET_OPTION_ID.equals(option.getId()));
                }
            }
        }
        return categories;
    }

    public static void removeRecipeBookToggle(Collection<FavoriteMenuEntry> original) {
        if (original == null || original.isEmpty()) return;
        for (FavoriteMenuEntry entry : original) {
            if (!(entry instanceof SubMenuEntry submenu) || !hasTranslationKey(submenu.text, DISPLAY_KEY)) continue;
            REISubMenuEntryAccessor access = (REISubMenuEntryAccessor) submenu;
            List<FavoriteMenuEntry> filtered = new ArrayList<>(access.norecipebooksystem$getEntries());
            filtered.removeIf(child -> child instanceof ToggleMenuEntry toggle
                    && hasTranslationKey(toggle.text, REMOVE_RECIPE_BOOK_KEY));
            access.norecipebooksystem$setEntries(filtered);
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

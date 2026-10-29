package net.codertcy.norecipebooksystem.common.compat;

import java.util.Objects;

/** Immutable client installation flags, not recipe-viewer readiness. */
public record RecipeViewerMods(boolean jei, boolean rrv, boolean rei) {
    private static RecipeViewerMods current;

    public static void initialize(RecipeViewerMods mods) {
        current = Objects.requireNonNull(mods);
    }

    public static RecipeViewerMods current() {
        return Objects.requireNonNull(current, "Recipe viewer mixin plugin has not initialized installation flags");
    }
}

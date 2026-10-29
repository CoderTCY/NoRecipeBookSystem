package net.codertcy.norecipebooksystem.common;

/** Loader-discovered optional mod state, initialized by the active compatibility plugin. */
public final class RecipeViewerHelper {
    private static volatile boolean initialized;
    private static boolean emiLoaded;

    private RecipeViewerHelper() {}

    public static void initialize(boolean loaded) {
        emiLoaded = loaded;
        initialized = true;
    }

    public static boolean isEmiLoaded() {
        if (!initialized) {
            throw new IllegalStateException("Recipe viewer mod discovery has not completed");
        }
        return emiLoaded;
    }
}

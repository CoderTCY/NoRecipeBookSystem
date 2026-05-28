package net.codertcy.norecipebooksystem.common;

/**
 * Utility for detecting optional recipe-viewer mods at runtime.
 *
 * <p>The detection result is cached in a static field so the classloader
 * probe executes at most once per classloader.  Call
 * {@link #isEmiLoaded()} from any mixin that needs to defer to EMI.
 */
public final class RecipeViewerHelper {
    /** No-op; static utility class. */
    private RecipeViewerHelper() {}

    private static final boolean EMI_LOADED = probeEmi();

    /**
     * Returns {@code true} if the EMI recipe viewer mod is available in the
     * current classloader.
     *
     * @return {@code true} when EMI is present
     */
    public static boolean isEmiLoaded() {
        return EMI_LOADED;
    }

    private static boolean probeEmi() {
        try {
            Class.forName("dev.emi.emi.config.EmiConfig", false,
                    RecipeViewerHelper.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}

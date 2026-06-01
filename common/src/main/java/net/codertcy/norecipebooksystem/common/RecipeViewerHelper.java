package net.codertcy.norecipebooksystem.common;

/**
 * Utility for detecting optional recipe-viewer mods at runtime.
 *
 * <p>The detection result is cached in a static field so the classloader
 * probe executes at most once per classloader.  Call
 * {@link #isEmiLoaded()} from any mixin that needs to defer to EMI.
 */
public final class RecipeViewerHelper {
    /** 禁止实例化；静态工具类。 */
    private RecipeViewerHelper() {}

    private static final boolean EMI_LOADED = probeEmi();

    /**
     * 当前 classloader 中是否存在 EMI 配方查看器模组。
     *
     * @return EMI 已加载时返回 {@code true}
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

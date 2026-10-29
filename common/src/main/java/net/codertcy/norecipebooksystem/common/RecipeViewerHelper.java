package net.codertcy.norecipebooksystem.common;

import net.codertcy.norecipebooksystem.common.compat.EivCompat;
import net.codertcy.norecipebooksystem.common.compat.JeiCompat;
import net.codertcy.norecipebooksystem.common.compat.RecipeViewerMods;
import net.codertcy.norecipebooksystem.common.compat.RrvCompat;

/**
 * Live recipe-viewer readiness checks. Only installation flags are cached;
 * connection and recipe-cache state are read on every call.
 */
public final class RecipeViewerHelper {
    private RecipeViewerHelper() {}

    public static boolean isJeiOnServer() {
        return RecipeViewerMods.current().jei() && JeiCompat.isOnServer();
    }

    public static boolean isRrvReady() {
        return RecipeViewerMods.current().rrv() && RrvCompat.isReady();
    }

    /** REI takes over recipe display as soon as it is installed on the client. */
    public static boolean isReiReady() {
        return RecipeViewerMods.current().rei();
    }

    public static boolean isEivReady() {
        return RecipeViewerMods.current().eiv() && EivCompat.isReady();
    }
}

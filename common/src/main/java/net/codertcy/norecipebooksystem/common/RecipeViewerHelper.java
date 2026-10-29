package net.codertcy.norecipebooksystem.common;

import net.codertcy.norecipebooksystem.common.compat.JeiCompat;
import net.codertcy.norecipebooksystem.common.compat.RecipeViewerMods;
import net.codertcy.norecipebooksystem.common.compat.RrvCompat;

/** Live readiness checks for recipe viewers published for Minecraft 26.3. */
public final class RecipeViewerHelper {
    private RecipeViewerHelper() {}

    public static boolean isJeiOnServer() {
        return RecipeViewerMods.current().jei() && JeiCompat.isOnServer();
    }

    public static boolean isRrvReady() {
        return RecipeViewerMods.current().rrv() && RrvCompat.isReady();
    }

    public static boolean isReiReady() {
        return RecipeViewerMods.current().rei();
    }
}

package net.codertcy.norecipebooksystem.common.compat;

import de.crafty.eiv.common.recipe.ClientRecipeCache;
import net.codertcy.norecipebooksystem.common.mixin.compact.EivClientRecipeCacheAccessor;

/** Loaded only after the loader confirms EIV is installed on the client. */
public final class EivCompat {
    private EivCompat() {}

    public static boolean isReady() {
        return !((EivClientRecipeCacheAccessor) ClientRecipeCache.INSTANCE)
                .norecipebooksystem$getRecipeMap().isEmpty();
    }
}

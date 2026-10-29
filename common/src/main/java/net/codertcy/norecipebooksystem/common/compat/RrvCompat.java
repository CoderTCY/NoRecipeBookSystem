package net.codertcy.norecipebooksystem.common.compat;

import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.client.recipe.InternalRecipeManager;
import net.codertcy.norecipebooksystem.common.mixin.compact.RrvClientRecipeCacheAccessor;

/** Loaded only after the loader confirms RRV is installed on the client. */
public final class RrvCompat {
    private RrvCompat() {}

    public static boolean isReady() {
        ClientRecipeCache cache = ClientRecipeCache.INSTANCE;
        RrvClientRecipeCacheAccessor access = (RrvClientRecipeCacheAccessor) cache;
        return (cache.localCacheBuilt() && !access.norecipebooksystem$getRecipeMap().isEmpty())
                || (InternalRecipeManager.INSTANCE.isRecipesSynced()
                && !access.norecipebooksystem$getServerEntryMap().isEmpty());
    }
}

package net.codertcy.norecipebooksystem.common.mixin.compact;

import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;
import java.util.LinkedHashMap;

@Mixin(value = ClientRecipeCache.class, remap = false)
public interface RrvClientRecipeCacheAccessor {
    @Accessor("recipeMap")
    HashMap<?, ?> norecipebooksystem$getRecipeMap();

    @Accessor("serverEntryMap")
    LinkedHashMap<?, ?> norecipebooksystem$getServerEntryMap();
}

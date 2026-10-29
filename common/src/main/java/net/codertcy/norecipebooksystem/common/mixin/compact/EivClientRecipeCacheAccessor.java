package net.codertcy.norecipebooksystem.common.mixin.compact;

import de.crafty.eiv.common.recipe.ClientRecipeCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(value = ClientRecipeCache.class, remap = false)
public interface EivClientRecipeCacheAccessor {
    @Accessor("recipeMap")
    HashMap<?, ?> norecipebooksystem$getRecipeMap();
}

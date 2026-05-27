package net.codertcy.norecipebooksystem.common.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

/**
 * Mixin that prevents {@link ServerPlayer} from awarding recipes.
 *
 * <p>Both {@code awardRecipes} and {@code awardRecipesByKey} are cancelled so
 * that the player never receives recipe unlocks on the server side. This ensures
 * the recipe book remains empty even when recipes are synced to the client.
 */
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    /** No-op; this class is a mixin target and should not be instantiated. */
    private ServerPlayerMixin() {}

    /**
     * Intercepts {@link ServerPlayer#awardRecipes(Collection)} and returns 0,
     * preventing any recipe from being awarded.
     *
     * @param holders the recipe holders being awarded (ignored)
     * @param cir     callback info used to return 0
     */
    @Inject(method = "awardRecipes", at = @At("HEAD"), cancellable = true)
    public void onAwardRecipes(Collection<RecipeHolder<?>> holders, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }

    /**
     * Intercepts {@link ServerPlayer#awardRecipesByKey(List)} and cancels it,
     * preventing recipes from being awarded by resource location key.
     *
     * @param recipes the recipe resource locations (ignored)
     * @param ci      callback info used to cancel the award
     */
    @Inject(method = "awardRecipesByKey", at = @At("HEAD"), cancellable = true)
    public void onAwardRecipesByKey(List<ResourceLocation> recipes, CallbackInfo ci) {
        ci.cancel();
    }
}

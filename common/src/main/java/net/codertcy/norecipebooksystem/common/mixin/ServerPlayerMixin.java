package net.codertcy.norecipebooksystem.common.mixin;

import net.minecraft.resources.Identifier;
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
 * Disables all recipe award notifications from server to client.
 *
 * <p>When a player unlocks recipes (e.g. via advancement or item pickup),
 * Minecraft normally sends a {@code awardRecipes} packet to the client which
 * triggers the "new recipe unlocked" toast. This mixin silently drops those
 * events so the player never sees recipe unlock notifications.
 */
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    /** Private constructor — mixin classes are never instantiated directly. */
    private ServerPlayerMixin() {}

    /**
     * Intercepts {@code ServerPlayer.awardRecipes(Collection)} at HEAD and returns 0,
     * preventing any recipe award packets from being sent for the given recipe holders.
     *
     * @param holders the recipes being awarded (ignored)
     * @param cir     callback returning 0, skipping the original method
     */
    @Inject(method = "awardRecipes", at = @At("HEAD"), cancellable = true)
    public void onAwardRecipes(Collection<RecipeHolder<?>> holders, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }

    /**
     * Intercepts {@code ServerPlayer.awardRecipesByKey(List)} at HEAD and cancels it,
     * preventing recipe award packets from being sent for recipes identified by key.
     *
     * @param recipes the recipe identifiers being awarded (ignored)
     * @param ci      callback cancelling the original method
     */
    @Inject(method = "awardRecipesByKey", at = @At("HEAD"), cancellable = true)
    public void onAwardRecipesByKey(List<Identifier> recipes, CallbackInfo ci) {
        ci.cancel();
    }
}

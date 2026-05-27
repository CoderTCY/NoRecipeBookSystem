package net.codertcy.norecipebooksystem.common.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Mixin that disables all persistence and network sync of the server-side
 * recipe book.
 *
 * <p>Three methods are intercepted:
 * <ul>
 *   <li>{@code toNbt} — returns an empty tag instead of saving recipe data</li>
 *   <li>{@code fromNbt} — cancels loading of saved recipe data</li>
 *   <li>{@code sendRecipes} — cancels sending recipes to the client</li>
 * </ul>
 * Together these ensure the recipe book never persists or synchronises.
 */
@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin {
    /** No-op; this class is a mixin target and should not be instantiated. */
    private ServerRecipeBookMixin() {}

    /**
     * Intercepts {@link ServerRecipeBook#toNbt()} and returns an empty
     * {@link CompoundTag}, discarding all recipe book data on save.
     *
     * @param cir callback info used to return the empty tag
     */
    @Inject(method = "toNbt", at = @At("HEAD"), cancellable = true)
    public void onSave(CallbackInfoReturnable<CompoundTag> cir) {
        cir.setReturnValue(new CompoundTag());
    }

    /**
     * Intercepts {@link ServerRecipeBook#fromNbt(CompoundTag, RecipeManager)}
     * and cancels it, preventing saved recipe data from being loaded.
     *
     * @param pTag           the NBT data to load from (ignored)
     * @param pRecipeManager the recipe manager (ignored)
     * @param ci             callback info used to cancel the load
     */
    @Inject(method = "fromNbt", at = @At("HEAD"), cancellable = true)
    public void onLoad(CompoundTag pTag, RecipeManager pRecipeManager, CallbackInfo ci) {
        ci.cancel();
    }

    /**
     * Intercepts {@link ServerRecipeBook#sendRecipes(ClientboundRecipePacket.State,
     * ServerPlayer, List)} and cancels it, blocking recipe data from being sent
     * to the client.
     *
     * @param pState   the sync state (ignored)
     * @param pPlayer  the target player (ignored)
     * @param pRecipes the recipes to send (ignored)
     * @param ci       callback info used to cancel the send
     */
    @Inject(method = "sendRecipes", at = @At("HEAD"), cancellable = true)
    public void onLoad(ClientboundRecipePacket.State pState, ServerPlayer pPlayer, List<ResourceLocation> pRecipes, CallbackInfo ci) {
        ci.cancel();
    }
}

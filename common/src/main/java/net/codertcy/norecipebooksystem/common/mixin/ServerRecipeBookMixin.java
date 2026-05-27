package net.codertcy.norecipebooksystem.common.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

/**
 * Neutralises the server-side recipe book persistence.
 *
 * <p>Vanilla Minecraft persists the player's recipe book state as a
 * {@link CompoundTag} via {@code addRecipes} / {@code loadUntrusted}.
 * This mixin ensures the saved data is always empty ({@code addRecipes}
 * returns an empty tag) and loaded data is discarded ({@code loadUntrusted}
 * is cancelled), so the recipe book stays clean across sessions.
 */
@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin {
    /** Private constructor — mixin classes are never instantiated directly. */
    private ServerRecipeBookMixin() {}

    /**
     * Intercepts {@code ServerRecipeBook.addRecipes()} at HEAD and returns an empty
     * {@link CompoundTag}, preventing any recipe data from being serialised.
     *
     * @param cir callback returning an empty tag, skipping the original method
     */
    @Inject(method = "addRecipes", at = @At("HEAD"), cancellable = true)
    public void onSave(CallbackInfoReturnable<CompoundTag> cir) {
        cir.setReturnValue(new CompoundTag());
    }

    /**
     * Intercepts {@code ServerRecipeBook.loadUntrusted(Packed, Predicate)} at HEAD
     * and cancels it, discarding any persisted recipe book data on load.
     *
     * @param packed    the serialised recipe book data (ignored)
     * @param predicate filter for which recipes to accept (ignored)
     * @param ci        callback cancelling the original method
     */
    @Inject(method = "loadUntrusted", at = @At("HEAD"), cancellable = true)
    public void onLoad(ServerRecipeBook.Packed packed, Predicate<ResourceKey<Recipe<?>>> predicate, CallbackInfo ci) {
        ci.cancel();
    }
}

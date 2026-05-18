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

@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin {
    @Inject(method = "addRecipes", at = @At("HEAD"), cancellable = true)
    public void onSave(CallbackInfoReturnable<CompoundTag> cir) {
        cir.setReturnValue(new CompoundTag());
    }

    @Inject(method = "loadUntrusted", at = @At("HEAD"), cancellable = true)
    public void onLoad(ServerRecipeBook.Packed packed, Predicate<ResourceKey<Recipe<?>>> predicate, CallbackInfo ci) {
        ci.cancel();
    }
}

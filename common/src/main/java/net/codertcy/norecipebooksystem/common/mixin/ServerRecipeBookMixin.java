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
    /** 私有构造方法 —— mixin 类不会被直接实例化。 */
    private ServerRecipeBookMixin() {}

    /**
     * 在 {@code ServerRecipeBook.addRecipes()} 的 HEAD 处拦截，并返回一个空的
     * {@link CompoundTag}，防止任何配方数据被序列化。
     *
     * @param cir 返回空标签的回调，用以跳过原方法
     */
    @Inject(method = "addRecipes", at = @At("HEAD"), cancellable = true)
    public void onSave(CallbackInfoReturnable<CompoundTag> cir) {
        cir.setReturnValue(new CompoundTag());
    }

    /**
     * 在 {@code ServerRecipeBook.loadUntrusted(Packed, Predicate)} 的 HEAD 处拦截
     * 并取消，丢弃加载时传入的任何已持久化的配方书数据。
     *
     * @param packed    序列化后的配方书数据（忽略）
     * @param predicate 用于过滤接受哪些配方的断言（忽略）
     * @param ci        取消原方法的回调
     */
    @Inject(method = "loadUntrusted", at = @At("HEAD"), cancellable = true)
    public void onLoad(ServerRecipeBook.Packed packed, Predicate<ResourceKey<Recipe<?>>> predicate, CallbackInfo ci) {
        ci.cancel();
    }
}

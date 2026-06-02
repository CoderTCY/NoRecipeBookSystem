package net.codertcy.norecipebooksystem.common.mixin;

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
 * Disables all server-side recipe book operations.
 *
 * <p>Two methods are intercepted:
 * <ul>
 *   <li>{@code addRecipes} — returns 0 so no recipes are added or sent to the client</li>
 *   <li>{@code loadUntrusted} — cancels loading of saved recipe data</li>
 * </ul>
 * Together these ensure the recipe book never persists, synchronises, or awards recipes.
 */
@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin {
    /** 私有构造方法 —— mixin 类不会被直接实例化。 */
    private ServerRecipeBookMixin() {}

    /**
     * Intercepts {@code ServerRecipeBook.addRecipes()} at HEAD and returns 0,
     * preventing any recipes from being added to {@code known}/{@code highlight},
     * any network packets from being sent, and any advancement criteria from firing.
     *
     * @param cir callback to return 0 and skip the original method
     */
    @Inject(method = "addRecipes", at = @At("HEAD"), cancellable = true)
    public void onAddRecipes(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
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

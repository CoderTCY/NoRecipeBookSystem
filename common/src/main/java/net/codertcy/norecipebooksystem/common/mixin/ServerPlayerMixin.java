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
    /** 私有构造方法 —— mixin 类不会被直接实例化。 */
    private ServerPlayerMixin() {}

    /**
     * 在 {@code ServerPlayer.awardRecipes(Collection)} 的 HEAD 处拦截，并直接返回 0，
     * 阻止为指定配方持有者发送任何配方授予数据包。
     *
     * @param holders 即将被授予的配方（忽略）
     * @param cir     返回 0 的回调，用以跳过原方法
     */
    @Inject(method = "awardRecipes", at = @At("HEAD"), cancellable = true)
    public void onAwardRecipes(Collection<RecipeHolder<?>> holders, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }

    /**
     * 在 {@code ServerPlayer.awardRecipesByKey(List)} 的 HEAD 处拦截并取消，
     * 阻止为通过键标识的配方发送配方授予数据包。
     *
     * @param recipes 即将被授予的配方标识（忽略）
     * @param ci      取消原方法的回调
     */
    @Inject(method = "awardRecipesByKey", at = @At("HEAD"), cancellable = true)
    public void onAwardRecipesByKey(List<Identifier> recipes, CallbackInfo ci) {
        ci.cancel();
    }
}

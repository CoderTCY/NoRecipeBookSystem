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
    /** 禁止实例化；此类仅供 Mixin 注入使用。 */
    private ServerPlayerMixin() {}

    /**
     * 拦截 {@link ServerPlayer#awardRecipes(Collection)} 并返回 0，
     * 阻止任何配方被授予玩家。
     *
     * @param holders 待授予的配方持有者（忽略）
     * @param cir     回调信息，用于返回 0
     */
    @Inject(method = "awardRecipes", at = @At("HEAD"), cancellable = true)
    public void onAwardRecipes(Collection<RecipeHolder<?>> holders, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }

    /**
     * 拦截 {@link ServerPlayer#awardRecipesByKey(List)} 并取消，
     * 阻止通过资源定位符键值授予配方。
     *
     * @param recipes 配方的资源定位符列表（忽略）
     * @param ci      回调信息，用于取消授予操作
     */
    @Inject(method = "awardRecipesByKey", at = @At("HEAD"), cancellable = true)
    public void onAwardRecipesByKey(List<ResourceLocation> recipes, CallbackInfo ci) {
        ci.cancel();
    }
}

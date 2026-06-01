package net.codertcy.norecipebooksystem.common.mixin;

import net.minecraft.stats.RecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin that prevents {@link RecipeBook} from copying data between instances.
 *
 * <p>Cancelling {@code copyOverData} ensures that the server-side recipe book
 * never propagates its state (e.g. when a player respawns or changes dimension),
 * keeping the recipe book effectively disabled.
 */
@Mixin(RecipeBook.class)
public class RecipeBookMixin {
    /** 禁止实例化；此类仅供 Mixin 注入使用。 */
    private RecipeBookMixin() {}

    /**
     * 拦截 {@link RecipeBook#copyOverData(RecipeBook)} 并取消，
     * 防止合成书状态在实例间复制。
     *
     * @param pOther 被复制的源合成书（忽略）
     * @param ci     回调信息，用于取消复制操作
     */
    @Inject(method = "copyOverData", at = @At("HEAD"), cancellable = true)
    public void onCopy(RecipeBook pOther, CallbackInfo ci) {
        ci.cancel();
    }
}

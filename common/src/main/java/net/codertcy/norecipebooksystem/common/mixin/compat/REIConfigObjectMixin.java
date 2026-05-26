package net.codertcy.norecipebooksystem.common.mixin.compat;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 使 REI 的 disableRecipeBook 选项始终返回 false，
 * 从而阻止 REI 尝试移除原版配方书按钮。
 * REI 是可选依赖，仅在 REI 加载时生效。
 */
@Pseudo
@Mixin(targets = "me.shedaniel.rei.impl.client.config.ConfigObjectImpl", priority = 2000)
public abstract class REIConfigObjectMixin {

    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();

    static {
        LOGGER.info(
            "[NoRecipeBookSystem] REI's disableRecipeBook has no effect — "
            + "NoRecipeBookSystem handles recipe book removal."
        );
    }

    @Inject(method = "doesDisableRecipeBook", at = @At("HEAD"), cancellable = true, remap = false)
    private void onDoesDisableRecipeBook(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}

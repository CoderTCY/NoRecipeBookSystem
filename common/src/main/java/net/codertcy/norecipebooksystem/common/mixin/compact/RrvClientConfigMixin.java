package net.codertcy.norecipebooksystem.common.mixin.compact;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forces RRV's {@code isRecipeBookButton()} to always return {@code true}, and
 * prevents {@code setRecipeBookButton(boolean)} from making any changes.
 *
 * <p>The config screen button is disabled by {@link RrvClientConfigScreenMixin}
 * so the user cannot even attempt to change this value through the UI.
 *
 * <p>{@code RecipeViewerHelper} does not influence this config in any way.
 *
 * <p>This mixin is optional ({@code require = 0}) — if RRV is not installed,
 * it is silently skipped.
 */
@Mixin(targets = "cc.cassian.rrv.common.config.instances.ClientConfig")
public class RrvClientConfigMixin {
    private RrvClientConfigMixin() {}

    @Inject(
            method = "isRecipeBookButton",
            at = @At("RETURN"),
            cancellable = true,
            require = 0
    )
    private void onIsRecipeBookButton(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(
            method = "setRecipeBookButton",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void onSetRecipeBookButton(@SuppressWarnings("unused") boolean value, CallbackInfo ci) {
        ci.cancel();
    }
}

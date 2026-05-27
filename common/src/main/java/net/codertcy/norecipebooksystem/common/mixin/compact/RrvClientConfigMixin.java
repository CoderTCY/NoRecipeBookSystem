package net.codertcy.norecipebooksystem.common.mixin.compact;

import net.codertcy.norecipebooksystem.common.RecipeViewerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forces RRV's {@code recipeBookButton} config to match whether the server has RRV installed.
 *
 * <ul>
 *   <li>Server has RRV ({@code rrv:recipe_request} channel registered) → button opens RRV overlay</li>
 *   <li>Server does NOT have RRV → button opens vanilla recipe book</li>
 * </ul>
 *
 * This mixin is optional ({@code require = 0}) — if RRV is not installed, it is silently skipped.
 */
@Mixin(targets = "cc.cassian.rrv.common.config.instances.ClientConfig")
public class RrvClientConfigMixin {
    /** Private constructor — mixin classes are never instantiated directly. */
    private RrvClientConfigMixin() {}

    @Inject(
            method = "isRecipeBookButton",
            at = @At("RETURN"),
            cancellable = true,
            require = 0
    )
    private void onIsRecipeBookButton(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(RecipeViewerHelper.isRrvOnServer());
    }
}

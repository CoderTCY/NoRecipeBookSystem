package net.codertcy.norecipebooksystem.common.mixin.compat;

import dev.emi.emi.com.unascribed.qdcss.QDCSS;
import net.codertcy.norecipebooksystem.common.compat.EmiConfigCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Keeps EMI's default recipe book action out of config loading and its UI. */
@Mixin(targets = "dev.emi.emi.config.EmiConfig", priority = 2000, remap = false)
public abstract class EMIConfigMixin {
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void registerRecipeBookFilter(CallbackInfo ci) {
        EmiConfigCompat.registerFilter();
    }

    // The opaque third argument is EMI's field token; we never inspect or invoke it.
    // The ConfigValue key identifies the same recipeBookAction assignment.
    @Inject(method = "assignField", at = @At("HEAD"), cancellable = true)
    private static void skipDefaultRecipeBookAction(QDCSS css, String key, @Coerce Object field, CallbackInfo ci) {
        if (EmiConfigCompat.shouldSkipAssignment(css, key)) {
            ci.cancel();
        }
    }
}

package net.codertcy.norecipebooksystem.common.mixin.compact;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forces REI's {@code doesDisableRecipeBook()} to always return {@code false}.
 *
 * <p>REI calls this method in several places to decide whether to remove the
 * vanilla recipe book button:
 * <ul>
 *   <li>The "Vanilla Recipe Book" toggle in the config screen</li>
 *   <li>The "Display Settings" → "Remove Recipe Book" toggle in the gear icon menu</li>
 *   <li>Runtime initialization that conditionally hides the recipe book button</li>
 * </ul>
 * By pinning the return value to {@code false}, we prevent REI from touching
 * the recipe book button at all, leaving full control to this mod's own
 * {@code ScreenMixin} / {@code RecipeBookComponentMixin}.
 *
 * <p>REI is optional — this mixin only activates when REI is present.
 *
 * @see REIConfigScreenMixin Removes the option from the config screen UI
 * @see REISubMenuMixin    Removes the toggle from the gear icon quick menu
 */
@Mixin(targets = "me.shedaniel.rei.impl.client.config.ConfigObjectImpl", priority = 2000)
public abstract class REIConfigObjectMixin {
    /** 禁止实例化；此类仅供 Mixin 注入使用。 */
    private REIConfigObjectMixin() {}

    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();

    static {
        LOGGER.info(
            "[NoRecipeBookSystem] REI's disableRecipeBook has no effect — "
            + "NoRecipeBookSystem handles recipe book removal."
        );
    }

    @Inject(method = "doesDisableRecipeBook", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void onDoesDisableRecipeBook(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}

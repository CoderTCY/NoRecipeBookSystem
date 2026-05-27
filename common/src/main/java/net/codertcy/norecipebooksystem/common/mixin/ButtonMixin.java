package net.codertcy.norecipebooksystem.common.mixin;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin that prevents the vanilla recipe book button from being pressed.
 *
 * <p>When a {@link Button} is pressed, this mixin checks whether it is the
 * recipe book button (an {@link ImageButton} using
 * {@link RecipeBookComponent#RECIPE_BUTTON_SPRITES}) and cancels the press if so.
 *
 * <p>If EMI is loaded the mixin defers to EMI's own button handling to avoid
 * conflicts.
 */
@Mixin(Button.class)
public class ButtonMixin {
    /** No-op; this class is a mixin target and should not be instantiated. */
    private ButtonMixin() {}

    private static final boolean EMI_LOADED = checkEmiLoaded();

    private static boolean checkEmiLoaded() {
        try {
            Class.forName("dev.emi.emi.config.EmiConfig", false, ButtonMixin.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Intercepts {@link Button#onPress()} and cancels it for the recipe book button.
     *
     * <p>Delegates to EMI when EMI is present so that EMI's own recipe book
     * configuration takes effect.
     *
     * @param ci callback info used to cancel the press
     */
    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    public void onPress(CallbackInfo ci) {
        if (EMI_LOADED) return; // EMI handles the button behavior

        Button button = (Button) (Object) this;

        if (button instanceof ImageButton image) {
            var sprites = ((ImageButtonAccessor) image).getSprites();
            if (sprites != null && sprites.equals(RecipeBookComponent.RECIPE_BUTTON_SPRITES)) {
                ci.cancel();
            }
        }
    }
}

package net.codertcy.norecipebooksystem.common.mixin;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Button.class)
public class ButtonMixin {

    private static final boolean EMI_LOADED = checkEmiLoaded();

    private static boolean checkEmiLoaded() {
        try {
            Class.forName("dev.emi.emi.config.EmiConfig", false, ButtonMixin.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    public void onPress(CallbackInfo ci) {
        if (EMI_LOADED) return; // EMI 接管按钮行为

        Button button = (Button) (Object) this;

        if (button instanceof ImageButton image) {
            var sprites = ((ImageButtonAccessor) image).getSprites();
            if (sprites != null && sprites.equals(RecipeBookComponent.RECIPE_BUTTON_SPRITES)) {
                ci.cancel();
            }
        }
    }
}

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
    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    public void onPress(CallbackInfo ci) {
        Button button = (Button) (Object) this;

        if (button instanceof ImageButton image) {
            var sprites = ((ImageButtonAccessor) image).getSprites();
            if (sprites != null && sprites.equals(RecipeBookComponent.RECIPE_BUTTON_SPRITES)) {
                ci.cancel();
            }
        }
    }
}

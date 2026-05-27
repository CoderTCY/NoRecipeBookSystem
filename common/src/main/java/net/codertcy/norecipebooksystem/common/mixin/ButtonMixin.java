package net.codertcy.norecipebooksystem.common.mixin;

import net.codertcy.norecipebooksystem.common.RecipeViewerHelper;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prevents the vanilla recipe book from opening when the recipe book button is pressed,
 * if JEI is active on the server or no recipe data (known recipes) has been received.
 *
 * <p>This is a safety net on top of {@link ScreenMixin}: even if the button was rendered,
 * pressing it will be silently cancelled here.
 */
@Mixin(Button.class)
public class ButtonMixin {
    /** Private constructor — mixin classes are never instantiated directly. */
    private ButtonMixin() {}

    /**
     * Intercepts every {@code Button#onPress()} call. If the pressed button is the recipe
     * book toggle button and JEI is detected on the server, or the client's known recipe
     * map is empty, the press event is cancelled.
     *
     * @param ci injection callback used to cancel the press event
     */
    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    public void onPress(CallbackInfo ci) {
        Button button = (Button) (Object) this;

        if (button instanceof ImageButton image && ((ImageButtonAccessor) image).getSprites() != null && ((ImageButtonAccessor) image).getSprites().equals(RecipeBookComponent.RECIPE_BUTTON_SPRITES)) {
            // Block recipe book button press if JEI is active on server, or no recipe data was sent
            if (RecipeViewerHelper.isJeiOnServer()) {
                ci.cancel();
                return;
            }
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                ClientRecipeBook book = (ClientRecipeBook) mc.player.getRecipeBook();
                if (((ClientRecipeBookAccessor) book).getKnown().isEmpty()) {
                    ci.cancel();
                }
            }
        }
    }
}

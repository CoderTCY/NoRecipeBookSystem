package net.codertcy.norecipebooksystem.common.mixin;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ScreenMixin {

    private static final boolean EMI_LOADED = checkEmiLoaded();

    private static boolean checkEmiLoaded() {
        try {
            Class.forName("dev.emi.emi.config.EmiConfig", false, ScreenMixin.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Inject(method = "addRenderableWidget", at = @At("HEAD"), cancellable = true)
    public <T extends GuiEventListener & Renderable & NarratableEntry> void onWidgetAdded(T widget, CallbackInfoReturnable<T> cir) {
        if (EMI_LOADED) return; // EMI 接管配方书按钮行为

        if (widget instanceof ImageButton image) {
            var sprites = ((ImageButtonAccessor) image).getSprites();
            if (sprites != null && sprites.equals(RecipeBookComponent.RECIPE_BUTTON_SPRITES)) {
                cir.setReturnValue(null);
            }
        }
    }
}

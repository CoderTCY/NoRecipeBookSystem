package net.codertcy.norecipebooksystem.common.mixin;

import net.codertcy.norecipebooksystem.common.util.JeiHelper;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
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
    @Inject(method = "addRenderableWidget", at = @At("HEAD"), cancellable = true)
    public <T extends GuiEventListener & Renderable & NarratableEntry> void onWidgetAdded(T widget, CallbackInfoReturnable<T> cir) {
        if (widget instanceof ImageButton image && ((ImageButtonAccessor) image).getSprites() != null && ((ImageButtonAccessor) image).getSprites().equals(RecipeBookComponent.RECIPE_BUTTON_SPRITES)) {
            // Block recipe book button if JEI is active on server, or no recipe data was sent
            if (JeiHelper.isJeiOnServer()) {
                cir.setReturnValue(null);
                return;
            }
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                ClientRecipeBook book = (ClientRecipeBook) mc.player.getRecipeBook();
                if (((ClientRecipeBookAccessor) book).getKnown().isEmpty()) {
                    cir.setReturnValue(null);
                }
            }
        }
    }
}

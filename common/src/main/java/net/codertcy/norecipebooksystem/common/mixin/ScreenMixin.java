package net.codertcy.norecipebooksystem.common.mixin;

import net.codertcy.norecipebooksystem.common.RecipeViewerHelper;
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

/**
 * Prevents the recipe book toggle button from being added to the screen when
 * JEI is active on the server, or when no recipe data (known recipes) has been
 * received from the server.
 *
 * <p>This mixin intercepts {@link Screen#addRenderableWidget} and removes the
 * recipe book button at insertion time, before it can ever be rendered.
 */
@Mixin(Screen.class)
public class ScreenMixin {
    /** Private constructor — mixin classes are never instantiated directly. */
    private ScreenMixin() {}

    /**
     * Intercepts widget addition. If the widget is an {@link ImageButton} whose sprite
     * matches {@code RecipeBookComponent#RECIPE_BUTTON_SPRITES}, and JEI is running on
     * the server or the client's known recipe map is empty, the widget is discarded
     * by returning {@code null}.
     *
     * @param <T>    the combined widget type
     * @param widget the widget being added to the screen
     * @param cir    callback used to return {@code null} and skip addition
     */
    @Inject(method = "addRenderableWidget", at = @At("HEAD"), cancellable = true)
    public <T extends GuiEventListener & Renderable & NarratableEntry> void onWidgetAdded(T widget, CallbackInfoReturnable<T> cir) {
        if (widget instanceof ImageButton image && ((ImageButtonAccessor) image).getSprites() != null && ((ImageButtonAccessor) image).getSprites().equals(RecipeBookComponent.RECIPE_BUTTON_SPRITES)) {
            // Block recipe book button if JEI is active on server, or no recipe data was sent
            if (RecipeViewerHelper.isJeiOnServer()) {
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

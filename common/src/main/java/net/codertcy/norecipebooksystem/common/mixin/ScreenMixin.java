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

/**
 * Mixin that prevents the vanilla recipe book button from being added to
 * any {@link Screen}.
 *
 * <p>When {@link Screen#addRenderableWidget} is called, this mixin checks
 * whether the widget is the recipe book button (identified by its sprite set)
 * and returns {@code null} instead of adding it.
 *
 * <p>If EMI is loaded the mixin defers to EMI's own handling to avoid
 * conflicts.
 */
@Mixin(Screen.class)
public class ScreenMixin {
    /** No-op; this class is a mixin target and should not be instantiated. */
    private ScreenMixin() {}

    private static final boolean EMI_LOADED = checkEmiLoaded();

    private static boolean checkEmiLoaded() {
        try {
            Class.forName("dev.emi.emi.config.EmiConfig", false, ScreenMixin.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Intercepts {@link Screen#addRenderableWidget} and blocks the recipe book
     * button from being added to the screen.
     *
     * <p>Delegates to EMI when EMI is present.
     *
     * @param <T>    the widget type
     * @param widget the widget being added
     * @param cir    callback info used to return {@code null} and block the addition
     */
    @Inject(method = "addRenderableWidget", at = @At("HEAD"), cancellable = true)
    public <T extends GuiEventListener & Renderable & NarratableEntry> void onWidgetAdded(T widget, CallbackInfoReturnable<T> cir) {
        if (EMI_LOADED) return; // EMI handles the recipe book button behavior

        if (widget instanceof ImageButton image) {
            var sprites = ((ImageButtonAccessor) image).getSprites();
            if (sprites != null && sprites.equals(RecipeBookComponent.RECIPE_BUTTON_SPRITES)) {
                cir.setReturnValue(null);
            }
        }
    }
}

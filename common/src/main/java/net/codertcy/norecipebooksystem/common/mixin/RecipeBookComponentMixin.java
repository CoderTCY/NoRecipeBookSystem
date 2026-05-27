package net.codertcy.norecipebooksystem.common.mixin;

import net.codertcy.norecipebooksystem.common.RecipeViewerHelper;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Suppresses the vanilla recipe book overlay when JEI or RRV is active on the server
 * and the client has no recipe data (known recipes map is empty).
 *
 * <p>Without this mixin, an empty recipe book overlay would still appear briefly even
 * when the button itself is hidden.
 */
@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {
    /** Private constructor — mixin classes are never instantiated directly. */
    private RecipeBookComponentMixin() {}

    /**
     * Intercepts {@code isVisibleAccordingToBookData()} after the original logic runs.
     * If a recipe viewer (JEI/RRV) is active on the server and the client's known recipe
     * map is empty, returns {@code false} to keep the book hidden.
     */
    @Inject(method = "isVisibleAccordingToBookData", at = @At("RETURN"), cancellable = true)
    private void onIsVisibleAccordingToBookData(CallbackInfoReturnable<Boolean> cir) {
        if (RecipeViewerHelper.isJeiOnServer() || RecipeViewerHelper.isRrvOnServer()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                ClientRecipeBook book = (ClientRecipeBook) mc.player.getRecipeBook();
                if (((ClientRecipeBookAccessor) book).getKnown().isEmpty()) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}

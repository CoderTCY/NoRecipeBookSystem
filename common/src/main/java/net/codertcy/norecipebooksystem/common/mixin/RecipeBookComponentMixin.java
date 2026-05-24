package net.codertcy.norecipebooksystem.common.mixin;

import net.codertcy.norecipebooksystem.common.util.JeiHelper;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {
    @Inject(method = "isVisibleAccordingToBookData", at = @At("RETURN"), cancellable = true)
    private void onIsVisibleAccordingToBookData(CallbackInfoReturnable<Boolean> cir) {
        if (JeiHelper.isJeiOnServer()) {
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

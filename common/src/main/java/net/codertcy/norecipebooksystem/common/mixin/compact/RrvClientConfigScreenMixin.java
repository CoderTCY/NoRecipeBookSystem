package net.codertcy.norecipebooksystem.common.mixin.compact;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hides the "Recipe Book Button" config option from RRV's settings screen,
 * since NoRecipeBookSystem now manages this behavior automatically
 * (via {@link RrvClientConfigMixin}).
 *
 * This mixin is optional ({@code require = 0}) — if RRV is not installed, it is silently skipped.
 */
@Mixin(targets = "cc.cassian.rrv.common.gui.ClientConfigScreen")
public class RrvClientConfigScreenMixin {
    /** Private constructor — mixin classes are never instantiated directly. */
    private RrvClientConfigScreenMixin() {}

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        Component targetLabel = Component.translatable("rrv.client_settings.recipe_book_button");

        for (var child : screen.children()) {
            if (child instanceof CycleButton<?> btn
                    && targetLabel.getString().equals(btn.getMessage().getString())) {
                btn.visible = false;
                break;
            }
        }
    }
}

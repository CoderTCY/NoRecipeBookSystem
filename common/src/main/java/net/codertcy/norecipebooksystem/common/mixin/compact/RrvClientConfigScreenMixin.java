package net.codertcy.norecipebooksystem.common.mixin.compact;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Locks the "Recipe Book Button" config option in RRV's settings screen.
 *
 * <p>Injects at {@code init()} RETURN and searches the widget tree for a
 * {@link CycleButton} whose display message starts with the setting's display
 * name ({@code "Recipe Book Button"}), then sets {@code active = false}.
 *
 * <p>This mixin is optional ({@code require = 0}) — if RRV is not installed,
 * it is silently skipped.
 */
@Mixin(targets = "cc.cassian.rrv.common.gui.ClientConfigScreen")
public class RrvClientConfigScreenMixin {
    private RrvClientConfigScreenMixin() {}

    @Inject(method = "init", at = @At("RETURN"), require = 0)
    private void onInit(CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        String settingName = Component.translatable("rrv.client_settings.recipe_book_button").getString();
        findAndDisable(screen.children(), settingName);
    }

    @Unique
    private static void findAndDisable(java.util.List<?> children, String settingName) {
        for (var child : children) {
            if (child instanceof CycleButton<?> btn) {
                if (btn.getMessage().getString().startsWith(settingName)) {
                    btn.active = false;
                    return;
                }
            }
            if (child instanceof ContainerEventHandler container) {
                findAndDisable(container.children(), settingName);
            }
        }
    }
}

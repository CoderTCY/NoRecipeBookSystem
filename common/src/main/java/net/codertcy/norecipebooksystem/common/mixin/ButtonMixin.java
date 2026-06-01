package net.codertcy.norecipebooksystem.common.mixin;

import net.codertcy.norecipebooksystem.common.RecipeViewerHelper;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin that prevents the vanilla recipe book button from being pressed.
 *
 * <p>When a {@link Button} is pressed, this mixin checks whether it is the
 * recipe book button (an {@link ImageButton} using
 * {@link RecipeBookComponent#RECIPE_BUTTON_SPRITES}) and cancels the press if so.
 *
 * <p>If EMI is loaded the mixin defers to EMI's own button handling to avoid
 * conflicts.
 */
@Mixin(Button.class)
public class ButtonMixin {
    /** 禁止实例化；此类仅供 Mixin 注入使用。 */
    private ButtonMixin() {}

    /**
     * 拦截 {@link Button#onPress()}，阻止合成书按钮被按下。
     *
     * <p>若 EMI 已加载则跳过，由 EMI 自行处理按钮行为。
     *
     * @param ci 回调信息，用于取消按钮按下
     */
    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    public void onPress(CallbackInfo ci) {
        if (RecipeViewerHelper.isEmiLoaded()) return; // 由 EMI 处理按钮行为

        Button button = (Button) (Object) this;

        if (button instanceof ImageButton image) {
            var sprites = ((ImageButtonAccessor) image).getSprites();
            if (sprites != null && sprites.equals(RecipeBookComponent.RECIPE_BUTTON_SPRITES)) {
                ci.cancel();
            }
        }
    }
}

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
    /** 私有构造方法 —— mixin 类不会被直接实例化。 */
    private ButtonMixin() {}

    /**
     * 拦截所有 {@code Button#onPress()} 调用。如果按下的按钮是配方书开关按钮，
     * 且检测到服务器端 JEI 已启用，或客户端的已知配方映射为空，则取消该按下事件。
     *
     * @param ci 用于取消按下事件的注入回调
     */
    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    public void onPress(CallbackInfo ci) {
        Button button = (Button) (Object) this;

        if (button instanceof ImageButton image && ((ImageButtonAccessor) image).getSprites() != null && ((ImageButtonAccessor) image).getSprites().equals(RecipeBookComponent.RECIPE_BUTTON_SPRITES)) {
            // 如果服务器端 JEI 已启用，或尚未收到任何配方数据，则屏蔽配方书按钮的按下
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

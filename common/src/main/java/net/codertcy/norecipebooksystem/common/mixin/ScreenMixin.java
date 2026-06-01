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
    /** 私有构造方法 —— mixin 类不会被直接实例化。 */
    private ScreenMixin() {}

    /**
     * 拦截组件添加逻辑。如果待添加的组件是 {@link ImageButton}，且其 sprite 与
     * {@code RecipeBookComponent#RECIPE_BUTTON_SPRITES} 相匹配，同时服务器端运行着 JEI
     * 或客户端的已知配方映射为空，则通过返回 {@code null} 丢弃该组件。
     *
     * @param <T>    组合后的组件类型
     * @param widget 即将被加入界面的组件
     * @param cir    用于返回 {@code null} 以跳过添加的回调
     */
    @Inject(method = "addRenderableWidget", at = @At("HEAD"), cancellable = true)
    public <T extends GuiEventListener & Renderable & NarratableEntry> void onWidgetAdded(T widget, CallbackInfoReturnable<T> cir) {
        if (widget instanceof ImageButton image && ((ImageButtonAccessor) image).getSprites() != null && ((ImageButtonAccessor) image).getSprites().equals(RecipeBookComponent.RECIPE_BUTTON_SPRITES)) {
            // 如果服务器端 JEI 已启用，或尚未收到任何配方数据，则屏蔽配方书按钮
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

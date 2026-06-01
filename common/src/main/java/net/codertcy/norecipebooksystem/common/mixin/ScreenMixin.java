package net.codertcy.norecipebooksystem.common.mixin;

import net.codertcy.norecipebooksystem.common.RecipeViewerHelper;
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
    /** 禁止实例化；此类仅供 Mixin 注入使用。 */
    private ScreenMixin() {}

    /**
     * 拦截 {@link Screen#addRenderableWidget}，阻止合成书按钮被添加到屏幕。
     *
     * <p>若 EMI 已加载则跳过，由 EMI 自行处理。
     *
     * @param <T>    控件类型
     * @param widget 正在添加的控件
     * @param cir    回调信息，用于返回 {@code null} 并阻止添加
     */
    @Inject(method = "addRenderableWidget", at = @At("HEAD"), cancellable = true)
    public <T extends GuiEventListener & Renderable & NarratableEntry> void onWidgetAdded(T widget, CallbackInfoReturnable<T> cir) {
        if (RecipeViewerHelper.isEmiLoaded()) return; // 由 EMI 处理合成书按钮行为

        if (widget instanceof ImageButton image) {
            var sprites = ((ImageButtonAccessor) image).getSprites();
            if (sprites != null && sprites.equals(RecipeBookComponent.RECIPE_BUTTON_SPRITES)) {
                cir.setReturnValue(null);
            }
        }
    }
}

package net.codertcy.norecipebooksystem.common.mixin;

import net.codertcy.norecipebooksystem.common.RecipeViewerHelper;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
     * 拦截组件添加逻辑。如果待添加的组件是配方书按钮，且需要屏蔽，则返回 {@code null}。
     *
     * @param <T>    组合后的组件类型
     * @param widget 即将被加入界面的组件
     * @param cir    用于返回 {@code null} 以跳过添加的回调
     */
    @Inject(method = "addRenderableWidget", at = @At("HEAD"), cancellable = true)
    public <T extends GuiEventListener & Renderable & NarratableEntry> void onWidgetAdded(T widget, CallbackInfoReturnable<T> cir) {
        if (shouldSuppressRecipeBookButton(widget)) {
            cir.setReturnValue(null);
        }
    }

    /**
     * Determines whether the recipe book button should be suppressed.
     *
     * <p>Shared by {@link ScreenMixin} and other client code to avoid
     * duplicating the identification and suppression logic.
     *
     * @param widget the widget being added or pressed
     * @return {@code true} if the widget is the recipe book button and should be hidden
     */
    @Unique
    private static boolean shouldSuppressRecipeBookButton(Object widget) {
        if (!isRecipeBookButton(widget)) return false;
        if (RecipeViewerHelper.isJeiOnServer() || RecipeViewerHelper.isRrvReady() || RecipeViewerHelper.isEivReady()) return true;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            ClientRecipeBook book = (ClientRecipeBook) mc.player.getRecipeBook();
            return ((ClientRecipeBookAccessor) book).getKnown().isEmpty();
        }
        return false;
    }

    /**
     * Checks whether a widget is the vanilla recipe book toggle button
     * by comparing its {@link net.minecraft.client.gui.components.WidgetSprites}
     * against {@link RecipeBookComponent#RECIPE_BUTTON_SPRITES}.
     */
    @Unique
    private static boolean isRecipeBookButton(Object widget) {
        if (widget instanceof net.minecraft.client.gui.components.ImageButton image) {
            var sprites = ((ImageButtonAccessor) image).getSprites();
            return sprites != null && sprites.equals(RecipeBookComponent.RECIPE_BUTTON_SPRITES);
        }
        return false;
    }
}

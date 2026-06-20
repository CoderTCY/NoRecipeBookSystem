package net.codertcy.norecipebooksystem.common.mixin;

import net.codertcy.norecipebooksystem.common.RecipeViewerHelper;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Suppresses the vanilla recipe book overlay when a recipe viewer (JEI/RRV/EIV)
 * is installed and ready. Matches the behavior of {@link ScreenMixin} which
 * suppresses the toggle button under the same conditions.
 */
@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {
    /** 私有构造方法 —— mixin 类不会被直接实例化。 */
    private RecipeBookComponentMixin() {}

    /**
     * 拦截 {@code isVisibleAccordingToBookData()} RETURN。
     * 如果任意配方查看器就绪，直接返回 {@code false} 隐藏侧边栏，
     * 与 {@link ScreenMixin} 的按钮抑制逻辑保持同步。
     */
    @Inject(method = "isVisibleAccordingToBookData", at = @At("RETURN"), cancellable = true)
    private void onIsVisibleAccordingToBookData(CallbackInfoReturnable<Boolean> cir) {
        if (RecipeViewerHelper.isJeiOnServer() || RecipeViewerHelper.isRrvReady() || RecipeViewerHelper.isEivReady()) {
            cir.setReturnValue(false);
        }
    }
}

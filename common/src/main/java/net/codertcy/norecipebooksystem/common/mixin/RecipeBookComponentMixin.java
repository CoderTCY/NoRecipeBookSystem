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
    /** 私有构造方法 —— mixin 类不会被直接实例化。 */
    private RecipeBookComponentMixin() {}

    /**
     * 拦截 {@code isVisibleAccordingToBookData()}，在原方法执行完毕后再插入逻辑。
     * 如果服务器端存在任意配方查看器（JEI/RRV），且客户端的已知配方映射为空，
     * 则返回 {@code false}，以保持配方书隐藏。
     */
    @Inject(method = "isVisibleAccordingToBookData", at = @At("RETURN"), cancellable = true)
    private void onIsVisibleAccordingToBookData(CallbackInfoReturnable<Boolean> cir) {
        if (RecipeViewerHelper.isJeiOnServer() || RecipeViewerHelper.isRrvReady() || RecipeViewerHelper.isEivReady()) {
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

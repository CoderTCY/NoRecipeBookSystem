package net.codertcy.norecipebooksystem.common.mixin;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin that prevents {@link ClientRecipeBook} from populating its recipe
 * collections.
 *
 * <p>By cancelling {@code setupCollections} at the head, the client-side recipe
 * book remains empty and never displays any recipes to the player.
 */
@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {
    /** 禁止实例化；此类仅供 Mixin 注入使用。 */
    private ClientRecipeBookMixin() {}

    /**
     * 拦截 {@link ClientRecipeBook#setupCollections(Iterable, RegistryAccess)}
     * 并取消，使客户端合成书始终保持为空。
     *
     * @param iterable      待处理的配方列表（忽略）
     * @param registryAccess 动态注册表访问器（忽略）
     * @param ci            回调信息，用于取消操作
     */
    @Inject(method = "setupCollections", at = @At("HEAD"), cancellable = true)
    public void onSetup(Iterable<Recipe<?>> iterable, RegistryAccess registryAccess, CallbackInfo ci) {
        ci.cancel();
    }
}

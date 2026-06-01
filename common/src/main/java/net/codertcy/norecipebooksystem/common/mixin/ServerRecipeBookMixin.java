package net.codertcy.norecipebooksystem.common.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Mixin that disables all persistence and network sync of the server-side
 * recipe book.
 *
 * <p>Three methods are intercepted:
 * <ul>
 *   <li>{@code toNbt} — returns an empty tag instead of saving recipe data</li>
 *   <li>{@code fromNbt} — cancels loading of saved recipe data</li>
 *   <li>{@code sendRecipes} — cancels sending recipes to the client</li>
 * </ul>
 * Together these ensure the recipe book never persists or synchronises.
 */
@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin {
    /** 禁止实例化；此类仅供 Mixin 注入使用。 */
    private ServerRecipeBookMixin() {}

    /**
     * 拦截 {@link ServerRecipeBook#toNbt()} 并返回空的 {@link CompoundTag}，
     * 丢弃所有合成书数据，阻止持久化保存。
     *
     * @param cir 回调信息，用于返回空标签
     */
    @Inject(method = "toNbt", at = @At("HEAD"), cancellable = true)
    public void onSave(CallbackInfoReturnable<CompoundTag> cir) {
        cir.setReturnValue(new CompoundTag());
    }

    /**
     * 拦截 {@link ServerRecipeBook#fromNbt(CompoundTag, RecipeManager)}
     * 并取消，阻止已保存的合成书数据被加载。
     *
     * @param pTag           待加载的 NBT 数据（忽略）
     * @param pRecipeManager 配方管理器（忽略）
     * @param ci             回调信息，用于取消加载
     */
    @Inject(method = "fromNbt", at = @At("HEAD"), cancellable = true)
    public void onLoad(CompoundTag pTag, RecipeManager pRecipeManager, CallbackInfo ci) {
        ci.cancel();
    }

    /**
     * 拦截 {@link ServerRecipeBook#sendRecipes(ClientboundRecipePacket.State,
     * ServerPlayer, List)} 并取消，阻止配方数据发送给客户端。
     *
     * @param pState   同步状态（忽略）
     * @param pPlayer  目标玩家（忽略）
     * @param pRecipes 待发送的配方列表（忽略）
     * @param ci       回调信息，用于取消发送
     */
    @Inject(method = "sendRecipes", at = @At("HEAD"), cancellable = true)
    public void onLoad(ClientboundRecipePacket.State pState, ServerPlayer pPlayer, List<ResourceLocation> pRecipes, CallbackInfo ci) {
        ci.cancel();
    }
}

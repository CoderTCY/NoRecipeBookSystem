package net.codertcy.norecipebooksystem.common.mixin;

import net.minecraft.stats.RecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin that prevents {@link RecipeBook} from copying data between instances.
 *
 * <p>Cancelling {@code copyOverData} ensures that the server-side recipe book
 * never propagates its state (e.g. when a player respawns or changes dimension),
 * keeping the recipe book effectively disabled.
 */
@Mixin(RecipeBook.class)
public class RecipeBookMixin {
    /** No-op; this class is a mixin target and should not be instantiated. */
    private RecipeBookMixin() {}

    /**
     * Intercepts {@link RecipeBook#copyOverData(RecipeBook)} and cancels it,
     * preventing any recipe book state from being copied.
     *
     * @param pOther the source recipe book to copy from (ignored)
     * @param ci     callback info used to cancel the copy
     */
    @Inject(method = "copyOverData", at = @At("HEAD"), cancellable = true)
    public void onCopy(RecipeBook pOther, CallbackInfo ci) {
        ci.cancel();
    }
}

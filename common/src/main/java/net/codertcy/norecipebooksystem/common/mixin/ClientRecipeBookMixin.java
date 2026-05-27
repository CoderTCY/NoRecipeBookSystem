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
    /** No-op; this class is a mixin target and should not be instantiated. */
    private ClientRecipeBookMixin() {}

    /**
     * Intercepts {@link ClientRecipeBook#setupCollections(Iterable, RegistryAccess)}
     * and cancels it so the recipe book stays empty on the client.
     *
     * @param iterable      the recipes to process (ignored)
     * @param registryAccess dynamic registry access (ignored)
     * @param ci            callback info used to cancel the setup
     */
    @Inject(method = "setupCollections", at = @At("HEAD"), cancellable = true)
    public void onSetup(Iterable<Recipe<?>> iterable, RegistryAccess registryAccess, CallbackInfo ci) {
        ci.cancel();
    }
}

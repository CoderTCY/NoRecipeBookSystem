package net.codertcy.norecipebooksystem.common.mixin.compact;

import me.shedaniel.rei.impl.client.gui.config.options.OptionCategory;
import net.codertcy.norecipebooksystem.common.compat.ReiCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

/**
 * Strips the "Vanilla Recipe Book" option ({@code accessibility.vanilla_recipe_book})
 * from REI's config screen at construction time.
 *
 * <p>This mixin intercepts the {@code categories} parameter passed to
 * {@code REIConfigScreen(Screen, List)} and walks the tree
 * {@code category → group → option}, removing the
 * {@code CompositeOption} whose ID equals {@code accessibility.vanilla_recipe_book}.
 *
 * <p>The interception happens at {@code @At("HEAD")} so that
 * {@code CollectionUtils.map(categories, OptionCategory::copy)} inside the
 * constructor naturally produces copied trees that already exclude the option.
 *
 * <p>REI is optional — this mixin only activates when REI is present.
 *
 * @see REIConfigObjectMixin Overrides the getter at the API level
 * @see REISubMenuMixin    Removes the toggle from the gear icon quick menu
 */
@Mixin(targets = "me.shedaniel.rei.impl.client.gui.config.REIConfigScreen", priority = 2000, remap = false)
public abstract class REIConfigScreenMixin {
    /** 禁止实例化；此类仅供 Mixin 注入使用。 */
    private REIConfigScreenMixin() {}

    /**
     * Removes the vanilla recipe book option before REI copies the category tree.
     */
    @ModifyVariable(
            method = "<init>(Lnet/minecraft/client/gui/screens/Screen;Ljava/util/List;)V",
            at = @At("HEAD"),
            argsOnly = true,
            remap = false
    )
    private static List<OptionCategory> modifyCategoriesParameter(List<OptionCategory> categories) {
        return ReiCompat.removeRecipeBookOption(categories);
    }
}

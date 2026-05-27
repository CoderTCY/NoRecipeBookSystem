package net.codertcy.norecipebooksystem.common.mixin;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor mixin for {@link ImageButton} that exposes the private
 * {@code sprites} field.
 *
 * <p>This is used by {@link ScreenMixin} and {@link ButtonMixin} to identify
 * whether a given {@link ImageButton} is the recipe book toggle button by
 * comparing its sprites against {@code RecipeBookComponent#RECIPE_BUTTON_SPRITES}.
 */
@Mixin(ImageButton.class)
public interface ImageButtonAccessor {
    /**
     * Returns the {@link WidgetSprites} assigned to this image button.
     *
     * @return the sprites field, or null if not yet initialised
     */
    @Accessor("sprites")
    WidgetSprites getSprites();
}

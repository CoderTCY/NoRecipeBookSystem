package net.codertcy.norecipebooksystem.common.mixin;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor mixin that exposes the {@code sprites} field of {@link ImageButton}.
 *
 * <p>Used by {@link ButtonMixin} and {@link ScreenMixin} to identify the recipe
 * book button by comparing its sprite set with
 * {@code RecipeBookComponent.RECIPE_BUTTON_SPRITES}.
 */
@Mixin(ImageButton.class)
public interface ImageButtonAccessor {
    /**
     * Returns the {@link WidgetSprites} configured for this image button.
     *
     * @return the widget sprites, or {@code null} if not set
     */
    @Accessor("sprites")
    WidgetSprites getSprites();
}

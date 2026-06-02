package net.codertcy.norecipebooksystem.common.mixin;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor mixin that exposes the {@code sprites} field of {@link ImageButton}.
 *
 * <p>Used by {@link ScreenMixin} to identify the recipe book button
 * by comparing its sprite set with
 * {@code RecipeBookComponent.RECIPE_BUTTON_SPRITES}.
 */
@Mixin(ImageButton.class)
public interface ImageButtonAccessor {
    /**
     * 获取此图像按钮配置的 {@link WidgetSprites}。
     *
     * @return 按钮的精灵图集，未设置时返回 {@code null}
     */
    @Accessor("sprites")
    WidgetSprites getSprites();
}

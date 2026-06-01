package net.codertcy.norecipebooksystem.common.mixin;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * Accessor mixin for {@link ClientRecipeBook} that exposes the private
 * {@code known} field, which maps recipe display IDs to their entries.
 *
 * <p>This is used by other mixins to check whether the client has received
 * any recipe data from the server before deciding to show/hide the book.
 */
@Mixin(ClientRecipeBook.class)
public interface ClientRecipeBookAccessor {
    /**
     * 返回已知的配方展示条目的内部映射。
     *
     * @return {@code known} 字段，初始化后不会为 null
     */
    @Accessor("known")
    Map<RecipeDisplayId, RecipeDisplayEntry> getKnown();
}

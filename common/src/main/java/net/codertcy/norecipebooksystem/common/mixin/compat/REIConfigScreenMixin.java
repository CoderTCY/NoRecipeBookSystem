package net.codertcy.norecipebooksystem.common.mixin.compat;

import me.shedaniel.rei.impl.client.gui.config.options.OptionCategory;
import net.codertcy.norecipebooksystem.common.compat.ReiConfigCompat;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

/** Filters the original option tree before REI copies it into the config screen. */
@Mixin(targets = "me.shedaniel.rei.impl.client.gui.config.REIConfigScreen", priority = 2000, remap = false)
public abstract class REIConfigScreenMixin {
    @ModifyVariable(
            target = @Desc(value = "<init>", args = {Screen.class, List.class}),
            at = @At("HEAD"),
            argsOnly = true
    )
    private static List<OptionCategory> modifyCategoriesParameter(List<OptionCategory> categories) {
        return ReiConfigCompat.filterCategories(categories);
    }
}

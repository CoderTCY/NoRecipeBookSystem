package net.codertcy.norecipebooksystem.common.compat;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/** Loader plugins provide installation flags before optional client mixins are inspected. */
public abstract class RecipeViewerMixinPlugin implements IMixinConfigPlugin {
    private RecipeViewerMods mods;

    protected abstract RecipeViewerMods detectMods();

    @Override
    public final void onLoad(String mixinPackage) {
        mods = detectMods();
        RecipeViewerMods.initialize(mods);
    }

    @Override
    public final boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        int nameStart = mixinClassName.lastIndexOf('.') + 1;
        if (mixinClassName.startsWith("Rrv", nameStart)) return mods.rrv();
        if (mixinClassName.startsWith("REI", nameStart)) return mods.rei();
        if (mixinClassName.startsWith("Eiv", nameStart)) return mods.eiv();
        throw new IllegalArgumentException("Unrecognized optional recipe viewer mixin: " + mixinClassName);
    }

    @Override
    public final String getRefMapperConfig() { return null; }

    @Override
    public final void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public final List<String> getMixins() { return null; }

    @Override
    public final void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public final void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}

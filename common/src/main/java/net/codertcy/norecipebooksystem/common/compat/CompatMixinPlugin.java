package net.codertcy.norecipebooksystem.common.compat;

import net.codertcy.norecipebooksystem.common.RecipeViewerHelper;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/** Contains no optional-mod or loader API references. */
public abstract class CompatMixinPlugin implements IMixinConfigPlugin {
    private boolean client;
    private boolean emiLoaded;
    private boolean reiLoaded;

    protected abstract boolean isActiveLoader();
    protected abstract boolean isClient();
    protected abstract boolean isModLoaded(String modId);

    @Override
    public final void onLoad(String mixinPackage) {
        if (!isActiveLoader()) return;
        client = isClient();
        emiLoaded = isModLoaded("emi");
        reiLoaded = isModLoaded("roughlyenoughitems");
        RecipeViewerHelper.initialize(emiLoaded);
    }

    @Override
    public final boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!client) return false;
        return (emiLoaded && mixinClassName.endsWith(".EMIConfigMixin"))
                || (reiLoaded && (mixinClassName.endsWith(".REIConfigObjectMixin")
                || mixinClassName.endsWith(".REIConfigScreenMixin")
                || mixinClassName.endsWith(".REISubMenuMixin")
                || mixinClassName.endsWith(".REISubMenuEntryAccessor")));
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

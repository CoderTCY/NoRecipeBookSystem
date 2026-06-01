package net.codertcy.norecipebooksystem.common;

/**
 * Entry point for the NoRecipeBookSystem mod.
 *
 * <p>This mod removes the vanilla recipe book button and the underlying recipe
 * book sync system (client-side and server-side), ensuring the recipe book is
 * completely disabled without affecting other recipe-related functionality.
 *
 * <p>Compatibility mixins for REI and EMI prevent those mods from overriding
 * this mod's recipe book removal behavior.
 */
public class NoRecipeBookSystem {
    /** 禁止实例化；此类仅暴露静态成员。 */
    private NoRecipeBookSystem() {}
    /** 在 fabric.mod.json 和 mods.toml 中使用的模组 ID。 */
    public static final String MODID = "norecipebooksystem";

    /**
     * 由各平台入口点（Fabric、Forge）在模组初始化期间调用。
     * 当前为空操作；实际逻辑位于 Mixin 中。
     */
    public static void init() {
    }
}

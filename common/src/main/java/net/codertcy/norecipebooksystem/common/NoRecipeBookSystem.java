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
    /** No-op; this class only exposes static members. */
    private NoRecipeBookSystem() {}
    /** The mod ID used in fabric.mod.json and mods.toml. */
    public static final String MODID = "norecipebooksystem";

    /**
     * Called by platform-specific entry points (Fabric, Forge) during mod
     * initialization. Currently a no-op; actual logic lives in mixins.
     */
    public static void init() {
    }
}

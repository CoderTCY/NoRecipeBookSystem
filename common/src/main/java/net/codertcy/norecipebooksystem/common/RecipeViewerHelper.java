package net.codertcy.norecipebooksystem.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Reflection-based utility to detect whether recipe viewer mods (JEI, RRV, REI, EIV)
 * are running on the connected server.
 *
 * <p>All methods use pure reflection — zero compile-time dependency on any recipe viewer mod.
 * Works with all loaders (Fabric, NeoForge, Forge).
 *
 * <p>{@code Method}/{@code Field} handles are cached after the first successful lookup to avoid
 * repeated {@code Class.forName}/{@code getMethod}/{@code getDeclaredField} overhead on hot paths
 * (e.g. every-tick visibility checks).
 *
 * <p>If the target mod is not installed, or not connected, safely returns {@code false}.
 */
public final class RecipeViewerHelper {
    /**
     * 基于反射的配方视图模组检测工具类，用于判断已连接服务器上是否运行了
     * JEI / RRV / REI / EIV 等配方视图模组。
     *
     * <p>所有方法均使用纯反射实现，对任何配方视图模组均无编译期依赖。
     * 支持所有加载器（Fabric、NeoForge、Forge）。
 *
 * <p>REI（Roughly Enough Items）仅检测客户端是否安装了 REI，
 * 因为 REI 在客户端安装后就会接管配方显示，
 * 无需等待服务端同步。
     *
     * <p>{@code Method}/{@code Field} 句柄在首次成功查找后被缓存，避免热路径
     * （例如每 tick 可见性检查）上重复的 {@code Class.forName} 开销。
     *
     * <p>若目标模组未安装或未连接，安全地返回 {@code false}。
     */
    private RecipeViewerHelper() {}

    // ── JEI reflection handles (cached) ──────────────────────────────────────
    private static Method sJeiGetConnection;
    private static Method sJeiIsOnServer;

    /**
     * 检测已连接服务器上是否运行 JEI。
     *
     * @return {@code true} 表示客户端已安装 JEI 且所连服务器报告 {@code isJeiOnServer()} 为 true。
     */
    public static boolean isJeiOnServer() {
        try {
            if (sJeiGetConnection == null) {
                Class<?> clazz = Class.forName("mezz.jei.common.Internal");
                sJeiGetConnection = clazz.getMethod("getServerConnection");
            }
            Object connection = sJeiGetConnection.invoke(null);
            if (connection == null) return false;
            if (sJeiIsOnServer == null) {
                sJeiIsOnServer = connection.getClass().getMethod("isJeiOnServer");
            }
            return (boolean) sJeiIsOnServer.invoke(connection);
        } catch (Exception e) {
            return false;
        }
    }

    // ── RRV reflection handles (cached) ──────────────────────────────────────
    private static Class<?> sRrvInternalMgrClass;
    private static Field sRrvInternalMgrInstance;
    private static Field sRrvRecipesSynced;
    private static Class<?> sRrvCacheClass;
    private static Field sRrvCacheInstance;
    private static Field sRrvServerEntryMap;
    private static Field sRrvLocalCacheBuilt;
    private static Field sRrvRecipeMapField;

    /**
     * 检测 Reliable Recipe Viewer（RRV）是否已就绪。
     *
     * <p>RRV 允许客户端使用本地回退来加载配方。当本地回退启用时，
     * {@code ClientRecipeCache.recipeMap} 会被本地配方文件填充，RRV 可以独立于
     * 服务端正常工作。以下四种情况均视为"就绪"：
     * <ol>
     *   <li>本地回退已构建缓存（{@code localCacheBuilt = true}）且 {@code recipeMap} 非空</li>
     *   <li>曾向服务端请求过配方同步（{@code recipesSynced = true}）</li>
     *   <li>正在从服务端同步数据（{@code Status.isIdle() = false}）</li>
     *   <li>服务端数据已确认到达（{@code serverEntryMap} 非空）</li>
     * </ol>
     *
     * <p>检查顺序：
     * <ol>
     *   <li>如果本地回退已构建缓存 → 返回 {@code true}</li>
     *   <li>如果从未请求过服务端数据，且未启用本地回退 → 返回 {@code false}</li>
     *   <li>如果正在从服务端同步 → 返回 {@code true}</li>
     *   <li>如果服务端数据已确认到达 → 返回 {@code true}</li>
     *   <li>否则 → 返回 {@code false}</li>
     * </ol>
     *
     * @return {@code true} 表示 RRV 已就绪（已加载配方数据），可以安全地隐藏原版配方书
     */
    public static boolean isRrvReady() {
        try {
            if (sRrvInternalMgrClass == null) {
                sRrvInternalMgrClass = Class.forName(
                        "cc.cassian.rrv.client.recipe.InternalRecipeManager");
                sRrvInternalMgrInstance = sRrvInternalMgrClass.getField("INSTANCE");
                // recipesSynced is a private boolean field
                sRrvRecipesSynced = sRrvInternalMgrClass.getDeclaredField("recipesSynced");
                sRrvRecipesSynced.setAccessible(true);
                sRrvCacheClass = Class.forName(
                        "cc.cassian.rrv.client.recipe.ClientRecipeCache");
                sRrvCacheInstance = sRrvCacheClass.getField("INSTANCE");
                sRrvServerEntryMap = sRrvCacheClass.getDeclaredField("serverEntryMap");
                sRrvServerEntryMap.setAccessible(true);
                sRrvLocalCacheBuilt = sRrvCacheClass.getDeclaredField("localCacheBuilt");
                sRrvLocalCacheBuilt.setAccessible(true);
                sRrvRecipeMapField = sRrvCacheClass.getDeclaredField("recipeMap");
                sRrvRecipeMapField.setAccessible(true);
            }
            Object cache = sRrvCacheInstance.get(null);

            // Step 1: Local fallback cache is built → RRV has recipes even without server
            if ((boolean) sRrvLocalCacheBuilt.get(cache)) {
                Map<?, ?> recipeMap = (Map<?, ?>) sRrvRecipeMapField.get(cache);
                if (!recipeMap.isEmpty()) return true;
            }

            // Step 2: Never requested server data → server does not have RRV
            Object internalMgr = sRrvInternalMgrInstance.get(null);
            if (!(boolean) sRrvRecipesSynced.get(internalMgr)) return false;

            // Step 3: Server data confirmed in cache (guards against stale recipesSynced)
            Map<?, ?> serverEntryMap = (Map<?, ?>) sRrvServerEntryMap.get(cache);
            if (!serverEntryMap.isEmpty()) return true;

            // Step 5: Conservative fallback
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // ── REI reflection handles (cached) ──────────────────────────────────────
    private static boolean sReiChecked;
    private static boolean sReiInstalled;

    /**
     * 检测客户端是否安装了 REI（Roughly Enough Items）。
     *
     * <p>REI 与 JEI 不同，它没有暴露服务端检测的 API。
     * 但只要客户端安装了 REI，它就会接管配方显示（即使服务端没有安装
     * REI，REI 也会通过本地回退显示配方）。
     * 因此直接检查 REI 客户端核心类是否可加载即可。
     *
     * @return {@code true} 表示客户端已安装 REI
     */
    public static boolean isReiReady() {
        if (!sReiChecked) {
            sReiChecked = true;
            try {
                Class.forName("me.shedaniel.rei.api.client.REIRuntime");
                sReiInstalled = true;
            } catch (Exception e) {
                sReiInstalled = false;
            }
        }
        return sReiInstalled;
    }

    // ── EIV reflection handles (cached) ──────────────────────────────────────
    private static Class<?> sEivCacheClass;
    private static Field sEivCacheInstance;
    private static Field sEivRecipeMapField;

    /**
     * 检测 Extended Item View（EIV）是否已就绪。
     *
     * <p>EIV 要求客户端 和 服务端同时安装才能工作。该方法通过检查 EIV 的
     * 客户端配方缓存（{@code ClientRecipeCache.recipeMap}）是否已被
     * 服务端数据填充来判断服务端是否也安装了 EIV。
     *
     * <p>如果缓存中有配方条目，说明服务端已通过 {@code eiv:recipe_request}
     * 通道发送了配方数据，EIV 已就绪。
     *
     * <p>此外，如果 EIV 的 {@code ClientRecipeManager.Status.isIdle()} 返回
     * {@code false}，说明 EIV 正在从服务端同步数据，也认为 EIV 即将就绪。
     *
     * @return {@code true} 表示 EIV 客户端已安装，且已从服务端收到配方数据
     *         （或正在接收中），可以安全地隐藏原版配方书
     */
    public static boolean isEivReady() {
        try {
            if (sEivCacheClass == null) {
                sEivCacheClass = Class.forName("de.crafty.eiv.common.recipe.ClientRecipeCache");
                sEivCacheInstance = sEivCacheClass.getField("INSTANCE");
                sEivRecipeMapField = sEivCacheClass.getDeclaredField("recipeMap");
                sEivRecipeMapField.setAccessible(true);
            }

            // Check 1: recipe cache already populated → server sent data
            Object cache = sEivCacheInstance.get(null);
            Map<?, ?> recipeMap = (Map<?, ?>) sEivRecipeMapField.get(cache);
            if (!recipeMap.isEmpty()) return true;

            // Cache empty → not ready (was previously checking status.isIdle)
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}

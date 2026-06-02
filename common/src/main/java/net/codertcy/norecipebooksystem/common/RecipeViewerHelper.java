package net.codertcy.norecipebooksystem.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Reflection-based utility to detect whether recipe viewer mods (JEI, RRV) are running on the connected server.
 *
 * <p>All methods use pure reflection — zero compile-time dependency on any recipe viewer mod.
 * Works with all loaders (Fabric, NeoForge, Forge).
 *
 * <p>{@code Method} handles are cached after the first successful lookup to avoid repeated
 * {@code Class.forName}/{@code getMethod} overhead on hot paths (e.g. every-tick visibility checks).
 *
 * <p>If the target mod is not installed, or not connected, safely returns {@code false}.
 */
public final class RecipeViewerHelper {
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
    private static Field sRrvPacketTypeField;
    private static Object sRrvPacketType;
    private static Method sRrvCanSend;

    /**
     * 检测已连接服务器上是否运行 Reliable Recipe Viewer（RRV）。
     *
     * @return {@code true} 表示客户端已安装 RRV 且所连服务器已注册 {@code rrv:recipe_request} 网络通道。
     */
    public static boolean isRrvOnServer() {
        try {
            if (sRrvPacketTypeField == null) {
                Class<?> packetClass = Class.forName(
                        "cc.cassian.rrv.common.network.payload.ServerboundRequestRrvUpdate");
                sRrvPacketTypeField = packetClass.getField("TYPE");
            }
            if (sRrvPacketType == null) {
                sRrvPacketType = sRrvPacketTypeField.get(null);
            }
            if (sRrvCanSend == null) {
                Class<?> mgrClass = Class.forName(
                        "cc.cassian.rrv.client.ClientNetworkManager");
                sRrvCanSend = mgrClass.getMethod("canSend", sRrvPacketType.getClass());
            }
            return (boolean) sRrvCanSend.invoke(null, sRrvPacketType);
        } catch (Exception e) {
            return false;
        }
    }
}

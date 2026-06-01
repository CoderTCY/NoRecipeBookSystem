package net.codertcy.norecipebooksystem.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Reflection-based utility to detect whether recipe viewer mods (JEI, RRV) are running on the connected server.
 *
 * <p>All methods use pure reflection — zero compile-time dependency on any recipe viewer mod.
 * Works with all loaders (Fabric, NeoForge, Forge).
 *
 * <p>If the target mod is not installed, or not connected, safely returns {@code false}.
 */
public final class RecipeViewerHelper {
    private RecipeViewerHelper() {}

    /**
     * 检测已连接服务器上是否运行 JEI。
     *
     * @return {@code true} 表示客户端已安装 JEI 且所连服务器报告 {@code isJeiOnServer()} 为 true。
     */
    public static boolean isJeiOnServer() {
        try {
            Class<?> internalClass = Class.forName("mezz.jei.common.Internal");
            Method getServerConnection = internalClass.getMethod("getServerConnection");
            Object connection = getServerConnection.invoke(null);
            Method isJeiOnServer = connection.getClass().getMethod("isJeiOnServer");
            return (boolean) isJeiOnServer.invoke(connection);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检测已连接服务器上是否运行 Reliable Recipe Viewer（RRV）。
     *
     * @return {@code true} 表示客户端已安装 RRV 且所连服务器已注册 {@code rrv:recipe_request} 网络通道。
     */
    public static boolean isRrvOnServer() {
        try {
            Class<?> packetClass = Class.forName(
                    "cc.cassian.rrv.common.network.payload.ServerboundRequestRrvUpdate");
            Field typeField = packetClass.getField("TYPE");
            Object packetType = typeField.get(null);

            Class<?> mgrClass = Class.forName(
                    "cc.cassian.rrv.client.ClientNetworkManager");
            Method canSend = mgrClass.getMethod("canSend", packetType.getClass());
            return (boolean) canSend.invoke(null, packetType);
        } catch (Exception e) {
            return false;
        }
    }
}

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
     * Checks whether JEI is running on the connected server.
     *
     * @return {@code true} if JEI is installed on the client AND the connected server
     *         reports {@code isJeiOnServer()} = true.
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
     * Checks whether Reliable Recipe Viewer (RRV) is running on the connected server.
     *
     * @return {@code true} if RRV is installed on the client AND the connected server
     *         has registered the {@code rrv:recipe_request} network channel.
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

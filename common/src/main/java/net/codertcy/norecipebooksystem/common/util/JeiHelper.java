package net.codertcy.norecipebooksystem.common.util;

import java.lang.reflect.Method;

/**
 * Reflection-based utility to detect whether JEI is running on the connected server.
 *
 * Calls JEI's own {@code Internal.getServerConnection().isJeiOnServer()} via reflection.
 * Zero compile-time dependency on JEI — works with all loaders (Fabric, NeoForge, Forge).
 *
 * If JEI is not installed, or not connected, safely returns {@code false}.
 */
public final class JeiHelper {
    private JeiHelper() {}

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
}

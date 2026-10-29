package net.codertcy.norecipebooksystem.common.compat;

import mezz.jei.common.Internal;
import mezz.jei.common.network.IConnectionToServer;

/** Loaded only after the loader confirms JEI is installed on the client. */
public final class JeiCompat {
    private JeiCompat() {}

    public static boolean isOnServer() {
        IConnectionToServer connection;
        try {
            connection = Internal.getServerConnection();
        } catch (IllegalStateException notInitialized) {
            // JEI's public getter rejects the startup window before a connection exists.
            return false;
        }
        return connection != null && connection.isJeiOnServer();
    }
}

package net.alphaantileak.mcac.utils;

import net.alphaantileak.mcac.server.AntiCheatClient;
import net.alphaantileak.mcac.server.AntiCheatServer;

import java.net.InetSocketAddress;

public class PlayerUtils {
    public static boolean isMCACUser(InetSocketAddress addr) {
        for (AntiCheatClient client : AntiCheatServer.instance().clients) {
            if (client == null) continue; // don't know why this is in here but whatever
            if (client.getProxy() == null) continue;
            if (client.getProxy().getLocalAddr() == null) continue;

            if (client.getProxy().getLocalAddr().equals(addr)) {
                return true;
            }
        }

        return false;
    }
}

package net.alphaantileak.mcac;

import net.alphaantileak.mcac.listener.BukkitListner;
import net.alphaantileak.mcac.server.AntiCheatServer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.*;
import java.util.logging.Level;

public class BukkitPlugin extends JavaPlugin implements PluginMessageListener {
    private AntiCheatServer server;
    public boolean bungeeMode;
    public boolean forceAnticheat;
    public String antiCheatKickMsg;
    public List<String> permissions;
    public String tabListTag;
    public String chatNotifyYes;
    public String chatNotifyNo;
    public Map<UUID, PermissionAttachment> perms = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupConfig();
        saveConfig();

        if (!bungeeMode) {
            int myPort = getServer().getPort() - 1;
            getLogger().info("Launching " + getName() + " Server on TCP/" + myPort);
            server = new AntiCheatServer(myPort);

            try {
                server.start();
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Failed starting server", e);
            }

            getServer().getPluginManager().registerEvents(new BukkitListner(this), this);
        } else {
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, Constants.MESSAGE_CHANNEL);
            this.getServer().getMessenger().registerIncomingPluginChannel(this, Constants.MESSAGE_CHANNEL, this);
        }
    }

    private void setupConfig() {
        FileConfiguration cfg = getConfig();
        if (!cfg.contains("bungeemode")) cfg.set("bungeemode", false);
        bungeeMode = cfg.getBoolean("bungeemode");

        // force mode only works if bungeemode == false
        if (!cfg.contains("force-mode.enabled")) cfg.set("force-mode.enabled", false);
        forceAnticheat = cfg.getBoolean("force-mode.enabled");

        if (!cfg.contains("force-mode.kick-msg")) cfg.set("force-mode.kick-msg", "You need to use 4lphaAntiCheat client-side anti cheat to join this server.");
        antiCheatKickMsg = cfg.getString("force-mode.kick-msg");

        List<String> defaultPerms = new ArrayList<>();
        defaultPerms.add("mcac.user");
        if (!cfg.contains("permissions")) cfg.set("permissions", defaultPerms);
        permissions = cfg.getStringList("permissions");

        if (!cfg.contains("tablist.enabled")) cfg.set("tablist.enabled", false);
        if (!cfg.contains("tablist.tag")) cfg.set("tablist.tag", "[4AC]");
        if (cfg.getBoolean("tablist.enabled")) {
            tabListTag = cfg.getString("tablist.tag");
        }

        if (!cfg.contains("chat-notifications.enabled")) cfg.set("chat-notifications.enabled", false);
        if (!cfg.contains("chat-notifications.not-using")) cfg.set("chat-notifications.not-using", "You are not using 4lphaAntiCheat");
        if (!cfg.contains("chat-notifications.is-using")) cfg.set("chat-notifications.is-using", "You are using 4lphaAntiCheat");
        if (cfg.getBoolean("chat-notifications.enabled")) {
            chatNotifyNo = cfg.getString("chat-notifications.not-using");
            chatNotifyYes = cfg.getString("chat-notifications.is-using");
        }
    }

    @Override
    public void onDisable() {
        if (server != null) server.stop();
    }

    @Override
    public void onPluginMessageReceived(String ch, Player player, byte[] msg) {
        if (!ch.equals(Constants.MESSAGE_CHANNEL)) {
            return;
        }

        if (msg.length != 1) {
            return;
        }

        if (msg[0] == 1) {
            setupUser(player);
        } else if (forceAnticheat) {
            onNonUserJoin(player);
            player.kickPlayer(antiCheatKickMsg);
        } else {
            onNonUserJoin(player);
        }
    }

    public void setupUser(Player player) {
        PermissionAttachment attachment = player.addAttachment(this);
        perms.put(player.getUniqueId(), attachment);
        for (String permission : permissions) {
            attachment.setPermission(permission, true);
        }

        if (tabListTag != null) {
            player.setPlayerListName(tabListTag + " " + player.getPlayerListName());
        }
        if (chatNotifyYes != null) {
            player.sendMessage(chatNotifyYes);
        }
    }

    public void onNonUserJoin(Player player) {
        if (chatNotifyNo != null) {
            player.sendMessage(chatNotifyNo);
        }
    }

    public void destroyUser(Player player) {
        PermissionAttachment perm = perms.get(player.getUniqueId());
        if (perm == null) return;
        for (String permission : permissions) {
            perm.unsetPermission(permission);
        }
        player.removeAttachment(perm);
        player.recalculatePermissions();
        perms.remove(player.getUniqueId());
    }
}

package net.alphaantileak.mcac;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.alphaantileak.mcac.commands.MasterCommand;
import net.alphaantileak.mcac.listener.BukkitListener;
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
    public String tabListPreFix;
    public String tabListSufFix;
    public String chatNotifyYes;
    public String chatNotifyNo;
    public String nickNamePreFix;
    public String nickNameSufFix;
    public Map<UUID, PermissionAttachment> perms = new HashMap<>();
    public Map<UUID, Boolean> isUser = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupConfig();
        saveConfig();
        registerCommands();

        if (!bungeeMode) {
            int myPort = getServer().getPort() - 1;
            getLogger().info("Launching " + getName() + " Server on TCP/" + myPort);
            server = new AntiCheatServer(myPort);

            try {
                server.start();
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Failed starting server", e);
            }

            getServer().getPluginManager().registerEvents(new BukkitListener(this), this);
        } else {
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, Constants.MESSAGE_CHANNEL);
            this.getServer().getMessenger().registerIncomingPluginChannel(this, Constants.MESSAGE_CHANNEL, this);
        }
    }

    private void registerCommands() {
        getServer().getPluginCommand("mcac").setExecutor(new MasterCommand(this));
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
        if (!permissions.contains("mcac.user")) permissions.add("mcac.user"); // used internally

        if (!cfg.contains("tablist.enabled")) cfg.set("tablist.enabled", false);
        if (!cfg.contains("tablist.prefix")) cfg.set("tablist.prefix", "[4AC] ");
        if (!cfg.contains("tablist.suffix")) cfg.set("tablist.suffix", "");
        if (cfg.getBoolean("tablist.enabled")) {
            tabListPreFix = cfg.getString("tablist.prefix").replace('&', '§');
            tabListSufFix = cfg.getString("tablist.suffix").replace('&', '§');
        }

        if (!cfg.contains("nickname.enabled")) cfg.set("nickname.enabled", false);
        if (!cfg.contains("nickname.prefix")) cfg.set("nickname.prefix", "[4AC] ");
        if (!cfg.contains("nickname.suffix")) cfg.set("nickname.suffix", "");
        if (cfg.getBoolean("nickname.enabled")) {
            nickNamePreFix = cfg.getString("nickname.prefix").replace('&', '§');
            nickNameSufFix = cfg.getString("nickname.suffix").replace('&', '§');
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

        ByteArrayDataInput in = ByteStreams.newDataInput(msg);
        String subchannel = in.readUTF();
        if (subchannel.equals(Constants.SUBCHANNEL)) {
            boolean use = in.readBoolean();

            if (use) {
                setupUser(player);
            } else if (forceAnticheat) {
                onNonUserJoin(player);
                player.kickPlayer(antiCheatKickMsg);
            } else {
                onNonUserJoin(player);
            }
        }


    }

    public void setupUser(Player player) {
        PermissionAttachment attachment = player.addAttachment(this);
        perms.put(player.getUniqueId(), attachment);
        for (String permission : permissions) {
            attachment.setPermission(permission, true);
        }

        if (tabListPreFix != null) {
            player.setPlayerListName(tabListPreFix + player.getPlayerListName() + tabListSufFix);
        }
        if (nickNamePreFix != null) {
            player.setDisplayName(nickNamePreFix + player.getDisplayName() + nickNameSufFix);
        }
        if (chatNotifyYes != null) {
            player.sendMessage(chatNotifyYes);
        }
        isUser.put(player.getUniqueId(), true);
    }

    public void onNonUserJoin(Player player) {
        if (chatNotifyNo != null) {
            player.sendMessage(chatNotifyNo);
        }
        isUser.put(player.getUniqueId(), false);
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
        isUser.remove(player.getUniqueId());
    }
}

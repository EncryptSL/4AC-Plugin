package net.alphaantileak.mcac;

import net.alphaantileak.mcac.listener.BungeeListener;
import net.alphaantileak.mcac.server.AntiCheatServer;
import net.alphaantileak.mcac.utils.BungeeUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class BungeePlugin extends Plugin {
    private AntiCheatServer server;

    public boolean forceAnticheat;
    public String antiCheatKickMsg;

    @Override
    public void onEnable() {
        BungeeUtils.RUNNING_ON_BUNGEE = true;

        try {
            setupConfig();
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Couldn't load or save config", e);
        }

        ProxyServer.getInstance().registerChannel(Constants.MESSAGE_CHANNEL);
        ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeeListener(this));

        int bungeePort = -1;
        for (ListenerInfo listenerInfo : ProxyServer.getInstance().getConfig().getListeners()) {
            bungeePort = listenerInfo.getHost().getPort();
        }

        if (bungeePort == -1) {
            getLogger().severe("Couldn't determine BungeeCord port");
            return;
        }

        int myPort = bungeePort - 1;
        getLogger().info("Launching " + getDescription().getName() + " Server on TCP/" + myPort);
        server = new AntiCheatServer(myPort);

        try {
            server.start();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed starting server", e);
        }
    }

    private void setupConfig() throws IOException {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        File cfgFile = new File(getDataFolder(), "config.yml");
        if (!cfgFile.exists()) cfgFile.createNewFile();

        Configuration cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(cfgFile);

        // force mode only works if bungeemode == false
        if (!cfg.contains("force-mode.enabled")) cfg.set("force-mode.enabled", false);
        forceAnticheat = cfg.getBoolean("force-mode.enabled");

        if (!cfg.contains("force-mode.kick-msg")) cfg.set("force-mode.kick-msg", "You need to use 4lphaAntiCheat client-side anti cheat to join this server.");
        antiCheatKickMsg = cfg.getString("force-mode.kick-msg");

        ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, cfgFile);
    }

    @Override
    public void onDisable() {
        if (server != null) server.stop();

        ProxyServer.getInstance().unregisterChannel(Constants.MESSAGE_CHANNEL);
    }
}

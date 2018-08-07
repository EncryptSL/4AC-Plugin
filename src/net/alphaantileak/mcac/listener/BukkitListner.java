package net.alphaantileak.mcac.listener;

import net.alphaantileak.mcac.BukkitPlugin;
import net.alphaantileak.mcac.utils.PlayerUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListner implements Listener {
    private final BukkitPlugin plugin;

    public BukkitListner(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerQuitEvent evt) {
        plugin.destroyUser(evt.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerJoinEvent evt) {
        if (!plugin.bungeeMode && plugin.forceAnticheat) {
            if (!PlayerUtils.isMCACUser(evt.getPlayer().getAddress())) {
                evt.getPlayer().kickPlayer(plugin.antiCheatKickMsg);
                return;
            }
        }
        if (PlayerUtils.isMCACUser(evt.getPlayer().getAddress())) {
            plugin.setupUser(evt.getPlayer());
        } else {
            plugin.onNonUserJoin(evt.getPlayer());
        }
    }
}

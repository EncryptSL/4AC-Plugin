package net.alphaantileak.mcac.listener;

import net.alphaantileak.mcac.BungeePlugin;
import net.alphaantileak.mcac.Constants;
import net.alphaantileak.mcac.utils.PlayerUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeListener implements Listener {

    private final BungeePlugin plugin;

    public BungeeListener(BungeePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PreLoginEvent evt) {
        if (plugin.forceAnticheat) {
            if (!PlayerUtils.isMCACUser(evt.getConnection().getAddress())) {
                evt.setCancelReason(new TextComponent(plugin.antiCheatKickMsg));
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(PostLoginEvent evt) {
        if (PlayerUtils.isMCACUser(evt.getPlayer().getAddress())) {
            evt.getPlayer().sendData(Constants.MESSAGE_CHANNEL, new byte[] { 1 });
        } else {
            evt.getPlayer().sendData(Constants.MESSAGE_CHANNEL, new byte[] { 0 });
        }
    }
}

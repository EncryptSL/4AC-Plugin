package net.alphaantileak.mcac.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.alphaantileak.mcac.BungeePlugin;
import net.alphaantileak.mcac.Constants;
import net.alphaantileak.mcac.utils.PlayerUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
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
    public void on(ServerConnectedEvent evt) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(Constants.SUBCHANNEL);
        if (PlayerUtils.isMCACUser(evt.getPlayer().getAddress())) {
            out.writeBoolean(true);
        } else {
            out.writeBoolean(false);
        }
        evt.getServer().sendData(Constants.MESSAGE_CHANNEL, out.toByteArray());
    }
}

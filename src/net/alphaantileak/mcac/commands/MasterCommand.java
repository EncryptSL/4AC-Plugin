package net.alphaantileak.mcac.commands;

import net.alphaantileak.mcac.BukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MasterCommand implements CommandExecutor {
    private final BukkitPlugin plugin;

    public MasterCommand(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdStr, String[] args) {
        if (!verifyNArgsPresent(sender, cmdStr, args, 1)) return true;
        if (args[0].equalsIgnoreCase("check")) {
            if (!verifyNArgsPresent(sender, cmdStr, args, 2)) return true;

            Player p = Bukkit.getPlayer(args[1]);
            if (p == null) {
                sender.sendMessage("Player not found");
                return true;
            }

            boolean isUser = plugin.isUser.getOrDefault(p.getUniqueId(), false);
            sender.sendMessage(p.getName() + " " + (isUser ? "is using" : "is not using") + " the client-side AntiCheat");
        }
        return true;
    }

    public boolean verifyNArgsPresent(CommandSender sender, String cmdString, String[] args, int n) {
        if (args.length < n) {
            printUsage(sender, cmdString);
            return false;
        }
        return true;
    }

    public void printUsage(CommandSender sender, String cmdStr) {
        sender.sendMessage("Usages:");
        sender.sendMessage(cmdStr + " check <player> - Checks if player is using the AntiCheat");
    }
}

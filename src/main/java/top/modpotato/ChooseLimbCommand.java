// SPDX-License-Identifier: GPL-3.0-or-later
package top.modpotato;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChooseLimbCommand implements CommandExecutor {
    private final AmputationPlugin plugin;

    public ChooseLimbCommand(AmputationPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("amputation.chooselimb")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /chooselimb <player>");
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }

        plugin.untwistPlayer(target);
        plugin.getLimbSelectionGUI().openGUI(target);
        sender.sendMessage(ChatColor.GREEN + "Opened limb selection menu for " + target.getName());
        return true;
    }
} 
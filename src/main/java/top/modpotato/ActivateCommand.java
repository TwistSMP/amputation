// SPDX-License-Identifier: GPL-3.0-or-later
package top.modpotato;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ActivateCommand implements CommandExecutor {
    private final AmputationPlugin plugin;

    public ActivateCommand(AmputationPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("amputation.activate")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        plugin.activatePlugin();
        sender.sendMessage(ChatColor.GREEN + "The plugin has been activated.");
        return true;
    }
} 
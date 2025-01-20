// SPDX-License-Identifier: GPL-3.0-or-later
package top.modpotato;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;

public class ReTwistTimerCommand implements CommandExecutor {
    private final AmputationPlugin plugin;
    private BukkitRunnable timerTask;
    private int remainingTime;
    private int cycleSeconds; // Store the cycle duration
    private static final int DEFAULT_HOURS = 1;
    private static final int SECONDS_PER_HOUR = 3600;

    public ReTwistTimerCommand(AmputationPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("amputation.retwisttimer")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("end")) {
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
                plugin.getServer().broadcastMessage(ChatColor.RED + "Re-twist timer has been stopped!");
            } else {
                sender.sendMessage(ChatColor.RED + "No active re-twist timer to stop!");
            }
            return true;
        }

        double hours;
        if (args.length == 0) {
            hours = DEFAULT_HOURS;
        } else {
            try {
                hours = Double.parseDouble(args[0]);
                if (hours <= 0) {
                    sender.sendMessage(ChatColor.RED + "Time must be greater than 0 hours!");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid time format! Please use a number in hours (decimals allowed).");
                return true;
            }
        }

        cycleSeconds = (int) (hours * SECONDS_PER_HOUR);
        startTimer();
        
        // Format message to show hours and minutes if it's a decimal
        String timeMessage;
        int fullHours = (int) hours;
        int minutes = (int) ((hours - fullHours) * 60);
        
        if (minutes > 0) {
            timeMessage = fullHours + " hour" + (fullHours != 1 ? "s" : "") + 
                         " and " + minutes + " minute" + (minutes != 1 ? "s" : "");
        } else {
            timeMessage = fullHours + " hour" + (fullHours != 1 ? "s" : "");
        }
        
        sender.sendMessage(ChatColor.GREEN + "Re-twist timer started! Will re-twist every " + timeMessage + ".");
        return true;
    }

    private void startTimer() {
        // Cancel any existing timer
        if (timerTask != null) {
            timerTask.cancel();
        }

        remainingTime = cycleSeconds;

        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTime <= 0) {
                    // Time's up! Force all players to re-choose
                    plugin.getServer().broadcastMessage(ChatColor.RED + "Time's up! All players must re-choose their limbs!");
                    
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        if (plugin.getSacrificedLimbs().containsKey(player.getUniqueId())) {
                            plugin.untwistPlayer(player);
                            plugin.getLimbSelectionGUI().openGUI(player);
                        }
                    }
                    
                    // Reset timer for next cycle
                    remainingTime = cycleSeconds;
                    plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Next re-twist cycle started!");
                    return;
                }

                // Broadcast remaining time at certain intervals
                int hoursRemaining = remainingTime / SECONDS_PER_HOUR;
                int minutesRemaining = (remainingTime % SECONDS_PER_HOUR) / 60;
                
                // Broadcast at: last 5 seconds, 30 seconds, 1 minute, 5 minutes, 15 minutes, 30 minutes, and every hour
                if (remainingTime <= 5 || remainingTime == 30 || remainingTime == 60 || 
                    remainingTime == 300 || remainingTime == 900 || remainingTime == 1800 || 
                    remainingTime % SECONDS_PER_HOUR == 0) {
                    String timeMessage;
                    if (remainingTime <= 5) {
                        timeMessage = remainingTime + " second" + (remainingTime != 1 ? "s" : "");
                    } else if (remainingTime < 60) {
                        timeMessage = remainingTime + " second" + (remainingTime != 1 ? "s" : "");
                    } else if (remainingTime < SECONDS_PER_HOUR) {
                        timeMessage = minutesRemaining + " minute" + (minutesRemaining != 1 ? "s" : "");
                    } else {
                        timeMessage = hoursRemaining + " hour" + (hoursRemaining != 1 ? "s" : "") + 
                                    (minutesRemaining > 0 ? " and " + minutesRemaining + " minute" + (minutesRemaining != 1 ? "s" : "") : "");
                    }
                    plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Re-twist in " + timeMessage + "!");
                }

                remainingTime--;
            }
        };

        timerTask.runTaskTimer(plugin, 0L, 20L); // Run every second
    }
} 
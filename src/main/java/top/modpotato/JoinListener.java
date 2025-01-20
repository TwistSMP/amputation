// SPDX-License-Identifier: GPL-3.0-or-later
package top.modpotato;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinListener implements Listener {
    private final AmputationPlugin plugin;

    public JoinListener(AmputationPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.isActive() && !plugin.getSacrificedLimbs().containsKey(event.getPlayer().getUniqueId())) {
            // Open GUI after a short delay to ensure player is fully loaded
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getLimbSelectionGUI().openGUI(event.getPlayer());
                }
            }.runTaskLater(plugin, 20L); // 1 second delay
        }
    }
} 
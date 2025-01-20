// SPDX-License-Identifier: GPL-3.0-or-later
package top.modpotato;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class EffectPersistenceListener implements Listener {
    private final AmputationPlugin plugin;

    public EffectPersistenceListener(AmputationPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // Get the player's limb type
        LimbType limb = plugin.getSacrificedLimbs().get(event.getPlayer().getUniqueId());
        if (limb != null) {
            // Reapply effects after respawn (1 tick delay to ensure player is fully respawned)
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.applyLimbEffects(event.getPlayer(), limb);
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        // If the player has a sacrificed limb and tries to drink milk, cancel it
        if (event.getItem().getType() == Material.MILK_BUCKET && plugin.getSacrificedLimbs().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot drink milk while you have a sacrificed limb!");
        }
    }
} 
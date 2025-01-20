// SPDX-License-Identifier: GPL-3.0-or-later
package top.modpotato;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GUIListener implements Listener {
    private final AmputationPlugin plugin;

    public GUIListener(AmputationPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(ChatColor.RED + "Choose a Limb")) {
            return;
        }

        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null) {
            return;
        }

        LimbType selectedLimb = null;
        switch (event.getCurrentItem().getType()) {
            case BONE:
                if (event.getSlot() == 0) {
                    selectedLimb = LimbType.MAINHAND;
                } else if (event.getSlot() == 6) {
                    selectedLimb = LimbType.LEG;
                }
                break;
            case CARROT_ON_A_STICK:
                selectedLimb = LimbType.OFFHAND;
                break;
            case ENDER_EYE:
                selectedLimb = LimbType.EYES;
                break;
            case NAUTILUS_SHELL:
                selectedLimb = LimbType.LUNG;
                break;
            case RED_DYE:
                selectedLimb = LimbType.HEART;
                break;
        }

        if (selectedLimb != null) {
            plugin.sacrificeLimb(player, selectedLimb);
            player.sendMessage(ChatColor.RED + "Your " + selectedLimb.toString().toLowerCase() + " has been chosen.");
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(ChatColor.RED + "Choose a Limb")) {
            return;
        }

        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        
        // If plugin is active and player hasn't made a choice yet, reopen the GUI
        if (plugin.isActive() && !plugin.getSacrificedLimbs().containsKey(player.getUniqueId())) {
            // Reopen the GUI after a tick to prevent closing
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getLimbSelectionGUI().openGUI(player);
                    player.sendMessage(ChatColor.RED + "You must choose a limb.");
                }
            }.runTaskLater(plugin, 1L);
        }
    }
} 
// SPDX-License-Identifier: GPL-3.0-or-later
package top.modpotato;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.UUID;
import java.util.Random;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;

public class LimbEffectListener implements Listener {
    private final AmputationPlugin plugin;
    private final HashMap<UUID, BukkitRunnable> oxygenRunnables = new HashMap<>();
    private final HashMap<UUID, BukkitRunnable> sprintRunnables = new HashMap<>();
    private final Random random = new Random();

    public LimbEffectListener(AmputationPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        LimbType limb = plugin.getSacrificedLimbs().get(player.getUniqueId());
        
        if (limb == LimbType.OFFHAND) {
            // Block offhand interactions
            if (event.getHand() == EquipmentSlot.OFF_HAND) {
                event.setCancelled(true);
                return;
            }
            
            // Block bow usage
            ItemStack item = event.getItem();
            if (item != null && item.getType() == Material.BOW) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot use a bow without your offhand!");
                return;
            }
            
            // 5% chance to drop held item when using it
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK ||
                event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (random.nextDouble() < 0.05) { // 5% chance
                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                    if (heldItem != null && heldItem.getType() != Material.AIR) {
                        player.getWorld().dropItemNaturally(player.getLocation(), heldItem.clone());
                        player.getInventory().setItemInMainHand(null);
                        player.sendMessage("§cYou fumbled and dropped your item!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        LimbType limb = plugin.getSacrificedLimbs().get(event.getWhoClicked().getUniqueId());
        if (limb == LimbType.OFFHAND) {
            if (event.getSlot() == 40) { // Offhand slot
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHandSwap(PlayerSwapHandItemsEvent event) {
        LimbType limb = plugin.getSacrificedLimbs().get(event.getPlayer().getUniqueId());
        if (limb == LimbType.OFFHAND) {
            event.setCancelled(true);
        }
    }

    private void stopOxygenDrain(UUID playerId) {
        BukkitRunnable runnable = oxygenRunnables.remove(playerId);
        if (runnable != null) {
            runnable.cancel();
        }
    }

    private void stopSprintDrain(UUID playerId) {
        BukkitRunnable runnable = sprintRunnables.remove(playerId);
        if (runnable != null) {
            runnable.cancel();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!plugin.isActive()) return;
        
        LimbType limb = plugin.getSacrificedLimbs().get(player.getUniqueId());
        if (limb == LimbType.LUNG) {
            Block block = player.getLocation().getBlock();
            boolean inWater = block.isLiquid() || (block.getBlockData() instanceof Waterlogged && ((Waterlogged) block.getBlockData()).isWaterlogged());
            
            if (inWater) {
                // Only start a new runnable if one isn't already running
                if (!oxygenRunnables.containsKey(player.getUniqueId())) {
                    BukkitRunnable runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!player.isOnline() || !player.getLocation().getBlock().isLiquid()) {
                                stopOxygenDrain(player.getUniqueId());
                                return;
                            }
                            int currentAir = player.getRemainingAir();
                            if (currentAir > 0) {
                                player.setRemainingAir(Math.max(0, currentAir - 30));
                            }
                            
                            // Handle swimming hunger drain
                            if (player.isSwimming()) {
                                int foodLevel = player.getFoodLevel();
                                if (foodLevel > 0) {
                                    player.setFoodLevel(foodLevel - 1);
                                }
                            }
                        }
                    };
                    runnable.runTaskTimer(plugin, 0L, 5L);
                    oxygenRunnables.put(player.getUniqueId(), runnable);
                }
            } else {
                stopOxygenDrain(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        if (!plugin.isActive()) return;
        
        LimbType limb = plugin.getSacrificedLimbs().get(player.getUniqueId());
        if (limb == LimbType.LUNG) {
            if (event.isSprinting()) {
                // Only start a new runnable if one isn't already running
                if (!sprintRunnables.containsKey(player.getUniqueId())) {
                    BukkitRunnable runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!player.isSprinting() || !player.isOnline()) {
                                stopSprintDrain(player.getUniqueId());
                                return;
                            }
                            int foodLevel = player.getFoodLevel();
                            if (foodLevel > 0) {
                                player.setFoodLevel(foodLevel - 1);
                            }
                        }
                    };
                    runnable.runTaskTimer(plugin, 0L, 10L);
                    sprintRunnables.put(player.getUniqueId(), runnable);
                }
            } else {
                stopSprintDrain(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!plugin.isActive()) return;
        
        Player player = (Player) event.getEntity();
        LimbType limb = plugin.getSacrificedLimbs().get(player.getUniqueId());
        
        if (limb == LimbType.HEART && event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!plugin.isActive()) return;
        
        Player player = (Player) event.getEntity();
        LimbType limb = plugin.getSacrificedLimbs().get(player.getUniqueId());
        
        if (limb == LimbType.HEART && event.getNewEffect() != null && 
            event.getNewEffect().getType() == PotionEffectType.ABSORPTION) {
            event.setCancelled(true);
        }
    }
} 
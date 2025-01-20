// SPDX-License-Identifier: GPL-3.0-or-later
package top.modpotato;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.event.Listener;
import org.bukkit.attribute.Attribute;

public class AmputationPlugin extends JavaPlugin implements Listener {
    private HashMap<UUID, LimbType> sacrificedLimbs;
    private BukkitRunnable effectsTask;
    private LimbSelectionGUI limbSelectionGUI;
    private boolean isActive = false;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        
        sacrificedLimbs = new HashMap<>();
        limbSelectionGUI = new LimbSelectionGUI(this);
        
        // Load active state from config
        isActive = getConfig().getBoolean("active", false);
        
        // Register commands
        getCommand("untwist").setExecutor(new UntwistCommand(this));
        getCommand("activateamputation").setExecutor(new ActivateCommand(this));
        getCommand("chooselimb").setExecutor(new ChooseLimbCommand(this));
        getCommand("retwisttimer").setExecutor(new ReTwistTimerCommand(this));
        getCommand("amputationstatus").setExecutor(new StatusCommand(this));
        
        startEffectsLoop();
        
        // Register events
        getServer().getPluginManager().registerEvents(new LimbEffectListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new EffectPersistenceListener(this), this);
    }

    public void activatePlugin() {
        isActive = true;
        getConfig().set("active", true);
        saveConfig();
        
        // Force all online players to choose a limb
        getServer().getOnlinePlayers().forEach(player -> {
            if (!sacrificedLimbs.containsKey(player.getUniqueId())) {
                getLimbSelectionGUI().openGUI(player);
            }
        });
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void onDisable() {
        if (effectsTask != null) {
            effectsTask.cancel();
        }
        sacrificedLimbs.clear();
        saveConfig();
    }

    private void startEffectsLoop() {
        effectsTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    LimbType limb = sacrificedLimbs.get(player.getUniqueId());
                    if (limb != null) {
                        applyLimbEffects(player, limb);
                    }
                }
            }
        };
        effectsTask.runTaskTimer(this, 0L, 1200L); // Run every minute
    }

    public void applyLimbEffects(Player player, LimbType limb) {
        // Reset max health if not selecting heart (in case they had heart before)
        if (limb != LimbType.HEART) {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setViewDistance(getServer().getViewDistance()); // Reset to server default
        }

        switch (limb) {
            case LEG:
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1220, 2)); // Slowness III
                player.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).setBaseValue(0);
                break;
            case OFFHAND:
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1220, 2)); // Weakness III
                player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 1220, 1)); // Mining Fatigue II
                // Offhand blocking handled by event listener
                break;
            case MAINHAND:
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1220, 2)); // Weakness III
                // Reach reduction handled by attribute modifier
                player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).setBaseValue(0);
                player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE).setBaseValue(0);
                break;
            case EYES:
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1220, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 1220, 0));
                if (player.getViewDistance() > 1) {
                    player.setViewDistance(1); // Set to minimum view distance (1 chunk)
                }
                break;
            case LUNG:
                // Hunger drain handled by sprint event listener
                player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 1220, 0)); // Mining Fatigue I
                break;
            case HEART:
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(4.0);
                player.setHealth(4.0);
                break;
        }
    }

    public void sacrificeLimb(Player player, LimbType limb) {
        sacrificedLimbs.put(player.getUniqueId(), limb);
        applyLimbEffects(player, limb);
    }

    public void untwistPlayer(Player player) {
        sacrificedLimbs.remove(player.getUniqueId());
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.JUMP_BOOST);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.DARKNESS);
        
        // Reset view distance if they had sacrificed eyes
        if (sacrificedLimbs.get(player.getUniqueId()) == LimbType.EYES) {
            player.setViewDistance(getServer().getViewDistance());
        }
        
        // Reset attributes
        player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).setBaseValue(3);
        player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE).setBaseValue(4.5);
        player.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).setBaseValue(3);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    }

    public void untwistAll() {
        for (UUID uuid : sacrificedLimbs.keySet()) {
            Player player = getServer().getPlayer(uuid);
            if (player != null) {
                untwistPlayer(player);
            }
        }
        sacrificedLimbs.clear();
    }

    public HashMap<UUID, LimbType> getSacrificedLimbs() {
        return sacrificedLimbs;
    }

    public LimbSelectionGUI getLimbSelectionGUI() {
        return limbSelectionGUI;
    }

    public void resetPlugin() {
        // Reset all players
        untwistAll();
        
        // Clear data structures
        sacrificedLimbs.clear();
        
        // Cancel and restart tasks
        if (effectsTask != null) {
            effectsTask.cancel();
        }
        startEffectsLoop();
        
        // Force reload all online players if plugin is active
        if (isActive) {
            getServer().getOnlinePlayers().forEach(player -> 
                getLimbSelectionGUI().openGUI(player)
            );
        }
    }
}
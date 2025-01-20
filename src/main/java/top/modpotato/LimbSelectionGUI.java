// SPDX-License-Identifier: GPL-3.0-or-later
package top.modpotato;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

public class LimbSelectionGUI {
    private final AmputationPlugin plugin;
    private final String INVENTORY_TITLE = ChatColor.RED + "Choose a Limb";

    public LimbSelectionGUI(AmputationPlugin plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, INVENTORY_TITLE);

        // Main hand (Bone)
        ItemStack mainHand = new ItemStack(Material.BONE);
        ItemMeta mainHandMeta = mainHand.getItemMeta();
        mainHandMeta.setDisplayName(ChatColor.WHITE + "Main Hand");
        mainHand.setItemMeta(mainHandMeta);
        inv.setItem(0, mainHand);

        // Off hand (Carrot on a Stick)
        ItemStack offHand = new ItemStack(Material.CARROT_ON_A_STICK);
        ItemMeta offHandMeta = offHand.getItemMeta();
        offHandMeta.setDisplayName(ChatColor.WHITE + "Off Hand");
        offHand.setItemMeta(offHandMeta);
        inv.setItem(2, offHand);

        // Eyes (Eye of Ender)
        ItemStack eyes = new ItemStack(Material.ENDER_EYE);
        ItemMeta eyesMeta = eyes.getItemMeta();
        eyesMeta.setDisplayName(ChatColor.WHITE + "Eyes");
        eyes.setItemMeta(eyesMeta);
        inv.setItem(4, eyes);

        // Leg (Bone)
        ItemStack leg = new ItemStack(Material.BONE);
        ItemMeta legMeta = leg.getItemMeta();
        legMeta.setDisplayName(ChatColor.WHITE + "Leg");
        leg.setItemMeta(legMeta);
        inv.setItem(6, leg);

        // Lung (Nautilus Shell)
        ItemStack lung = new ItemStack(Material.NAUTILUS_SHELL);
        ItemMeta lungMeta = lung.getItemMeta();
        lungMeta.setDisplayName(ChatColor.WHITE + "Lung");
        lung.setItemMeta(lungMeta);
        inv.setItem(3, lung);

        // Heart (Red Dye)
        ItemStack heart = new ItemStack(Material.RED_DYE);
        ItemMeta heartMeta = heart.getItemMeta();
        heartMeta.setDisplayName(ChatColor.RED + "Heart");
        heart.setItemMeta(heartMeta);
        inv.setItem(8, heart);

        player.openInventory(inv);
    }
} 
package org.cyberluca.cyberBan.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.cyberluca.cyberBan.CyberBanPlugin;

import java.util.Arrays;

public class AdminGUI implements Listener {

    private final CyberBanPlugin plugin;

    public AdminGUI(CyberBanPlugin plugin) {
        this.plugin = plugin;
    }

    public static void open(Player p, CyberBanPlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.DARK_RED + "CyberBan Admin");
        ItemStack ban = new ItemStack(Material.RED_BED);
        ItemMeta bm = ban.getItemMeta();
        bm.setDisplayName(ChatColor.RED + "Bannen");
        bm.setLore(Arrays.asList(ChatColor.GRAY + "Benutze /ban <spieler> <dauer|perm> [grund]"));
        ban.setItemMeta(bm);

        ItemStack unban = new ItemStack(Material.GREEN_BED);
        ItemMeta um = unban.getItemMeta();
        um.setDisplayName(ChatColor.GREEN + "Entbannen");
        um.setLore(Arrays.asList(ChatColor.GRAY + "Benutze /unban <spieler>"));
        unban.setItemMeta(um);

        ItemStack mute = new ItemStack(Material.PAPER);
        ItemMeta mm = mute.getItemMeta();
        mm.setDisplayName(ChatColor.YELLOW + "Muten");
        mm.setLore(Arrays.asList(ChatColor.GRAY + "Benutze /mute <spieler> <dauer|perm> [grund]"));
        mute.setItemMeta(mm);

        inv.setItem(2, ban);
        inv.setItem(4, mute);
        inv.setItem(6, unban);

        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().contains("CyberBan Admin")) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        if (!e.getCurrentItem().hasItemMeta()) return;
        String name = e.getCurrentItem().getItemMeta().getDisplayName();
        Player clicker = (Player) e.getWhoClicked();
        if (name.contains("Bannen")) {
            clicker.sendMessage(ChatColor.YELLOW + "Benutze: /ban <spieler> <dauer|perm> [grund]");
        } else if (name.contains("Entbannen")) {
            clicker.sendMessage(ChatColor.YELLOW + "Benutze: /unban <spieler>");
        } else if (name.contains("Muten")) {
            clicker.sendMessage(ChatColor.YELLOW + "Benutze: /mute <spieler> <dauer|perm> [grund]");
        }
        clicker.closeInventory();
    }
}

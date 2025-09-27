package org.cyberluca.cyberBan.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyberluca.cyberBan.CyberBanPlugin;
import org.cyberluca.cyberBan.gui.AdminGUI;

public class CyberBanGUICommand implements CommandExecutor {

    private final CyberBanPlugin plugin;

    public CyberBanGUICommand(CyberBanPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur im Spiel nutzbar.");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("cyberban.gui")) {
            p.sendMessage(plugin.getMessageUtil().get("no-permission"));
            return true;
        }
        AdminGUI.open(p, plugin);
        return true;
    }
}

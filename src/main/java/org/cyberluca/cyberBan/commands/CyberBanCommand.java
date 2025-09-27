package org.cyberluca.cyberBan.commands;

import org.bukkit.command.*;
import org.cyberluca.cyberBan.CyberBanPlugin;

public class CyberBanCommand implements CommandExecutor {

    private final CyberBanPlugin plugin;
    public CyberBanCommand(CyberBanPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("cyberban.reload")) {
                sender.sendMessage(plugin.getMessageUtil().get("no-permission")); return true;
            }
            plugin.getMessageUtil().reload();
            sender.sendMessage(plugin.getMessageUtil().get("reload-success"));
            return true;
        }
        sender.sendMessage(plugin.getMessageUtil().get("cyberban-usage"));
        return true;
    }
}

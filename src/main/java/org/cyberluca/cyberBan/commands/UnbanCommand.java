package org.cyberluca.cyberBan.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.cyberluca.cyberBan.CyberBanPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UnbanCommand implements CommandExecutor {

    private final CyberBanPlugin plugin;

    public UnbanCommand(CyberBanPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("cyberban.unban")) {
            sender.sendMessage(plugin.getMessageUtil().get("no-permission"));
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(plugin.getMessageUtil().get("usage-unban"));
            return true;
        }

        OfflinePlayer off = Bukkit.getOfflinePlayer(args[0]);
        UUID uuid = off.getUniqueId();
        if (!plugin.getBanManager().isBanned(uuid)) {
            sender.sendMessage(plugin.getMessageUtil().get("not-banned"));
            return true;
        }

        plugin.getBanManager().unbanPlayer(uuid);
        Map<String,String> ph = new HashMap<>();
        ph.put("player", off.getName());
        ph.put("actor", sender.getName());
        sender.sendMessage(plugin.getMessageUtil().format("unban-success", ph));
        return true;
    }
}

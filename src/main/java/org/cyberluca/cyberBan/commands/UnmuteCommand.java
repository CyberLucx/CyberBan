package org.cyberluca.cyberBan.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.cyberluca.cyberBan.CyberBanPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UnmuteCommand implements CommandExecutor {

    private final CyberBanPlugin plugin;

    public UnmuteCommand(CyberBanPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("cyberban.unmute")) {
            sender.sendMessage(plugin.getMessageUtil().get("no-permission")); return true;
        }
        if (args.length != 1) { sender.sendMessage(plugin.getMessageUtil().get("usage-unmute")); return true; }

        OfflinePlayer off = Bukkit.getOfflinePlayer(args[0]);
        UUID uuid = off.getUniqueId();
        if (!plugin.getMuteManager().isMuted(uuid)) {
            sender.sendMessage(plugin.getMessageUtil().get("not-muted")); return true;
        }
        plugin.getMuteManager().unmutePlayer(uuid);
        Map<String,String> ph = new HashMap<>(); ph.put("player", off.getName()); ph.put("actor", sender.getName());
        sender.sendMessage(plugin.getMessageUtil().format("unmute-success", ph));
        return true;
    }
}

package org.cyberluca.cyberBan.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.cyberluca.cyberBan.CyberBanPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SoftBanCommand implements CommandExecutor {

    private final CyberBanPlugin plugin;

    public SoftBanCommand(CyberBanPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("cyberban.softban")) {
            sender.sendMessage(plugin.getMessageUtil().get("no-permission")); return true;
        }
        if (args.length < 1) { sender.sendMessage(plugin.getMessageUtil().get("usage-softban")); return true; }
        OfflinePlayer off = Bukkit.getOfflinePlayer(args[0]);
        UUID uuid = off.getUniqueId();
        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args,1,args.length)) : "Kein Grund angegeben";

        // kick, then ban for short time (like 1 minute)
        if (Bukkit.getPlayer(uuid) != null) Bukkit.getPlayer(uuid).kickPlayer(plugin.getMessageUtil().formatKick(reason, sender.getName(), System.currentTimeMillis()+60_000));
        plugin.getBanManager().banPlayer(uuid, reason, sender.getName(), 60_000L);

        Map<String,String> ph = new HashMap<>(); ph.put("player", off.getName()); ph.put("banner", sender.getName()); ph.put("reason", reason);
        sender.sendMessage(plugin.getMessageUtil().format("softban-success", ph));
        return true;
    }
}

package org.cyberluca.cyberBan.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.cyberluca.cyberBan.CyberBanPlugin;

import java.util.HashMap;
import java.util.Map;

public class IPBanCommand implements CommandExecutor {

    private final CyberBanPlugin plugin;

    public IPBanCommand(CyberBanPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("cyberban.ipban")) {
            sender.sendMessage(plugin.getMessageUtil().get("no-permission")); return true;
        }
        if (args.length < 2) { sender.sendMessage(plugin.getMessageUtil().get("usage-ipban")); return true; }

        String target = args[0];
        String reason = String.join(" ", java.util.Arrays.copyOfRange(args,1,args.length));
        // try to get IP from online player first
        String ip = null;
        Player p = Bukkit.getPlayerExact(target);
        if (p != null && p.getAddress() != null) ip = p.getAddress().getAddress().getHostAddress();
        if (ip == null) ip = target; // treat as direct IP

        plugin.getIpBanManager().banIp(ip, -1L, reason);
        plugin.getHistoryManager().addEntry(ip, "IP-BAN", sender.getName(), -1L, reason);

        Map<String,String> ph = new HashMap<>();
        ph.put("player", target); ph.put("ip", ip); ph.put("reason", reason);
        sender.sendMessage(plugin.getMessageUtil().format("ipban-success", ph));

        // kick matching online players
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getAddress() != null && online.getAddress().getAddress().getHostAddress().equals(ip)) {
                online.kickPlayer(plugin.getMessageUtil().formatKick(reason, sender.getName(), -1L));
            }
        }
        return true;
    }
}

package org.cyberluca.cyberBan.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.cyberluca.cyberBan.CyberBanPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BanCommand implements CommandExecutor {

    private final CyberBanPlugin plugin;

    public BanCommand(CyberBanPlugin plugin) {
        this.plugin = plugin;
    }

    private long parseDuration(String s) {
        if (s.equalsIgnoreCase("perm") || s.equalsIgnoreCase("permanent")) return -1L;
        try {
            char unit = s.charAt(s.length()-1);
            long number = Long.parseLong(s.substring(0, s.length()-1));
            return switch (unit) {
                case 's' -> number * 1000L;
                case 'm' -> number * 60_000L;
                case 'h' -> number * 3_600_000L;
                case 'd' -> number * 86_400_000L;
                default -> -2L;
            };
        } catch (Exception e) {
            return -2L;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("cyberban.ban")) {
            sender.sendMessage(plugin.getMessageUtil().get("no-permission"));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.getMessageUtil().get("usage-ban"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        UUID uuid = target.getUniqueId();
        long duration = parseDuration(args[1]);
        if (duration == -2L) {
            sender.sendMessage(plugin.getMessageUtil().get("invalid-duration"));
            return true;
        }
        String reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args,2,args.length)) : "Kein Grund angegeben";
        if (plugin.getBanManager().isBanned(uuid)) {
            sender.sendMessage(plugin.getMessageUtil().get("already-banned"));
            return true;
        }

        plugin.getBanManager().banPlayer(uuid, reason, sender.getName(), duration);
        Map<String,String> ph = new HashMap<>();
        ph.put("player", target.getName());
        ph.put("banner", sender.getName());
        ph.put("reason", reason);
        ph.put("duration", duration == -1 ? "Permanent" : plugin.getMessageUtil().formatTime(duration));
        sender.sendMessage(plugin.getMessageUtil().format("ban-success", ph));
        return true;
    }
}

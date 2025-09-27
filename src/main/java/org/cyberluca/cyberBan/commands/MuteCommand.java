package org.cyberluca.cyberBan.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.cyberluca.cyberBan.CyberBanPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MuteCommand implements CommandExecutor {

    private final CyberBanPlugin plugin;

    public MuteCommand(CyberBanPlugin plugin) { this.plugin = plugin; }

    private long parseDuration(String s) {
        if (s.equalsIgnoreCase("perm")) return -1L;
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
        } catch (Exception e) { return -2L; }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("cyberban.mute")) {
            sender.sendMessage(plugin.getMessageUtil().get("no-permission")); return true;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.getMessageUtil().get("usage-mute")); return true;
        }
        OfflinePlayer off = Bukkit.getOfflinePlayer(args[0]);
        UUID uuid = off.getUniqueId();
        long duration = parseDuration(args[1]);
        if (duration == -2L) { sender.sendMessage(plugin.getMessageUtil().get("invalid-duration")); return true; }
        String reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args,2,args.length)) : "Kein Grund angegeben";

        plugin.getMuteManager().mutePlayer(uuid, reason, sender.getName(), duration);
        Map<String,String> ph = new HashMap<>();
        ph.put("player", off.getName()); ph.put("muter", sender.getName()); ph.put("reason", reason); ph.put("duration", duration==-1?"Permanent":plugin.getMessageUtil().formatTime(duration));
        sender.sendMessage(plugin.getMessageUtil().format("mute-success", ph));
        return true;
    }
}


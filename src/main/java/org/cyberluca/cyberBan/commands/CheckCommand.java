package org.cyberluca.cyberBan.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.cyberluca.cyberBan.CyberBanPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CheckCommand implements CommandExecutor {
    private final CyberBanPlugin plugin;
    public CheckCommand(CyberBanPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) { sender.sendMessage(plugin.getMessageUtil().get("usage-check")); return true; }
        OfflinePlayer off = Bukkit.getOfflinePlayer(args[0]);
        UUID uuid = off.getUniqueId();
        Map<String,String> ph = new HashMap<>();
        ph.put("player", off.getName());

        if (plugin.getBanManager().isBanned(uuid)) {
            ph.put("banner", plugin.getBanManager().getBanner(uuid));
            ph.put("reason", plugin.getBanManager().getBanReason(uuid));
            long end = plugin.getBanManager().getBanEnd(uuid);
            ph.put("duration", end == -1 ? "Permanent" : plugin.getMessageUtil().formatTime(end - System.currentTimeMillis()));
            sender.sendMessage(plugin.getMessageUtil().format("check-banned", ph));
            return true;
        }
        if (plugin.getMuteManager().isMuted(uuid)) {
            ph.put("muter", plugin.getMuteManager().getMuter(uuid));
            ph.put("reason", plugin.getMuteManager().getMuteReason(uuid));
            long end = plugin.getMuteManager().getMuteEnd(uuid);
            ph.put("duration", end == -1 ? "Permanent" : plugin.getMessageUtil().formatTime(end - System.currentTimeMillis()));
            sender.sendMessage(plugin.getMessageUtil().format("check-muted", ph));
            return true;
        }
        sender.sendMessage(plugin.getMessageUtil().format("check-clear", ph));
        return true;
    }
}

package org.cyberluca.cyberBan.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.cyberluca.cyberBan.CyberBanPlugin;

import java.util.List;
import java.util.Map;

public class MuteHistoryCommand implements CommandExecutor {
    private final CyberBanPlugin plugin;
    public MuteHistoryCommand(CyberBanPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) { sender.sendMessage(plugin.getMessageUtil().get("usage-mutehistory")); return true; }
        OfflinePlayer off = Bukkit.getOfflinePlayer(args[0]);
        List<Map<String,Object>> history = plugin.getHistoryManager().getHistory(off.getName());
        if (history.isEmpty()) {
            sender.sendMessage(plugin.getMessageUtil().get("history-empty")); return true;
        }
        sender.sendMessage(plugin.getMessageUtil().get("history-header").replace("%player%", off.getName()));
        for (Map<String,Object> entry : history) {
            sender.sendMessage("- " + entry.get("type") + " von " + entry.get("actor") + " grund: " + entry.get("grund"));
        }
        return true;
    }
}

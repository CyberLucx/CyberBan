package org.cyberluca.cyberBan.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cyberluca.cyberBan.CyberBanPlugin;

import java.io.File;
import java.util.Map;

public class MessageUtil {

    private final CyberBanPlugin plugin;
    private File messagesFile;
    private FileConfiguration cfg;

    public MessageUtil(CyberBanPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        try {
            this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
            if (!messagesFile.exists()) {
                plugin.saveResource("messages.yml", false);
            }
            cfg = YamlConfiguration.loadConfiguration(messagesFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Konnte messages.yml nicht laden: " + e.getMessage());
        }
    }

    public void reload() {
        load();
    }

    public String get(String path) {
        if (cfg == null) return color("&cNachrichten nicht geladen!");
        return color(cfg.getString(path, "&cNachricht '" + path + "' nicht gefunden."));
    }


    public String format(String path, Map<String, String> placeholders) {
        String template = get(path);
        if (placeholders == null || placeholders.isEmpty()) return template;
        String out = template;
        for (Map.Entry<String, String> e : placeholders.entrySet()) {
            String key = "%" + e.getKey() + "%";
            out = out.replace(key, e.getValue() == null ? "" : e.getValue());
        }
        return out;
    }

    public String color(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }


    public String formatKick(String reason, String banner, long endMillis) {
        String duration = endMillis == -1 ? "Permanent" : formatTime(endMillis - System.currentTimeMillis());
        return format("ban-kick", Map.of(
                "player", "",
                "banner", banner != null ? banner : "Konsole",
                "reason", reason != null ? reason : "Kein Grund angegeben",
                "duration", duration
        ));
    }

    public String formatTime(long millis) {
        if (millis <= 0) return "0s";
        long seconds = Math.max(0, millis / 1000);
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        if (days > 0) return days + "d " + hours + "h";
        if (hours > 0) return hours + "h " + minutes + "m";
        if (minutes > 0) return minutes + "m " + secs + "s";
        return secs + "s";
    }
}

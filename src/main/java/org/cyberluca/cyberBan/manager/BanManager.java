package org.cyberluca.cyberBan.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cyberluca.cyberBan.CyberBanPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BanManager {

    private final CyberBanPlugin plugin;
    private final File file;
    private FileConfiguration cfg;
    private final Map<UUID, BanData> bans = new HashMap<>();

    public BanManager(CyberBanPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "bans.yml");
        load();
    }

    private void load() {
        try {
            plugin.getDataFolder().mkdirs();
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cfg = YamlConfiguration.loadConfiguration(file);
        if (cfg.contains("bans")) {
            for (String key : cfg.getConfigurationSection("bans").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    long end = cfg.getLong("bans." + key + ".end", -1L);
                    String reason = cfg.getString("bans." + key + ".reason", "Unbekannt");
                    String banner = cfg.getString("bans." + key + ".banner", "Konsole");
                    bans.put(uuid, new BanData(reason, banner, end));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void saveBans() {
        if (cfg == null) cfg = YamlConfiguration.loadConfiguration(file);
        cfg.set("bans", null);
        for (Map.Entry<UUID, BanData> e : bans.entrySet()) {
            String key = e.getKey().toString();
            BanData d = e.getValue();
            cfg.set("bans." + key + ".end", d.end);
            cfg.set("bans." + key + ".reason", d.reason);
            cfg.set("bans." + key + ".banner", d.banner);
        }
        try {
            cfg.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void banPlayer(UUID uuid, String reason, String banner, long durationMillis) {
        long end = (durationMillis <= 0 ? -1 : System.currentTimeMillis() + durationMillis);
        bans.put(uuid, new BanData(reason, banner, end));
        saveBans();


        Bukkit.getPlayer(uuid).getPlayer();
        if (Bukkit.getPlayer(uuid) != null) {
            Bukkit.getPlayer(uuid).kickPlayer(plugin.getMessageUtil().formatKick(reason, banner, end));
        }

        plugin.getHistoryManager().addEntry(Bukkit.getOfflinePlayer(uuid).getName(), "BAN", banner, end, reason);
    }

    public void unbanPlayer(UUID uuid) {
        bans.remove(uuid);
        saveBans();
    }

    public boolean isBanned(UUID uuid) {
        BanData d = bans.get(uuid);
        if (d == null) return false;
        if (d.end == -1) return true;
        if (System.currentTimeMillis() > d.end) {
            bans.remove(uuid);
            saveBans();
            return false;
        }
        return true;
    }

    public String getBanReason(UUID uuid) {
        BanData d = bans.get(uuid);
        return d != null ? d.reason : null;
    }

    public String getBanner(UUID uuid) {
        BanData d = bans.get(uuid);
        return d != null ? d.banner : null;
    }

    public long getBanEnd(UUID uuid) {
        BanData d = bans.get(uuid);
        return d != null ? d.end : -1;
    }

    private static class BanData {
        final String reason;
        final String banner;
        final long end;
        BanData(String reason, String banner, long end) {
            this.reason = reason;
            this.banner = banner;
            this.end = end;
        }
    }
}

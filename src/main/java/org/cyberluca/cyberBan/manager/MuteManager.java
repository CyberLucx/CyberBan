package org.cyberluca.cyberBan.manager;

import org.cyberluca.cyberBan.CyberBanPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MuteManager {

    private final CyberBanPlugin plugin;
    private final File file;
    private FileConfiguration cfg;
    private final Map<UUID, MuteData> mutes = new HashMap<>();

    public MuteManager(CyberBanPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "mutes.yml");
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
        if (cfg.contains("mutes")) {
            for (String key : cfg.getConfigurationSection("mutes").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    long end = cfg.getLong("mutes." + key + ".end", -1L);
                    String reason = cfg.getString("mutes." + key + ".reason", "Unbekannt");
                    String muter = cfg.getString("mutes." + key + ".muter", "Konsole");
                    mutes.put(uuid, new MuteData(reason, muter, end));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void saveMutes() {
        if (cfg == null) cfg = YamlConfiguration.loadConfiguration(file);
        cfg.set("mutes", null);
        for (Map.Entry<UUID, MuteData> e : mutes.entrySet()) {
            String key = e.getKey().toString();
            MuteData d = e.getValue();
            cfg.set("mutes." + key + ".end", d.end);
            cfg.set("mutes." + key + ".reason", d.reason);
            cfg.set("mutes." + key + ".muter", d.muter);
        }
        try { cfg.save(file); } catch (IOException ex) { ex.printStackTrace(); }
    }

    public void mutePlayer(UUID uuid, String reason, String muter, long durationMillis) {
        long end = (durationMillis <= 0 ? -1 : System.currentTimeMillis() + durationMillis);
        mutes.put(uuid, new MuteData(reason, muter, end));
        saveMutes();
        plugin.getHistoryManager().addEntry(plugin.getServer().getOfflinePlayer(uuid).getName(), "MUTE", muter, end, reason);
    }

    public void unmutePlayer(UUID uuid) {
        mutes.remove(uuid);
        saveMutes();
    }

    public boolean isMuted(UUID uuid) {
        MuteData d = mutes.get(uuid);
        if (d == null) return false;
        if (d.end == -1) return true;
        if (System.currentTimeMillis() > d.end) {
            mutes.remove(uuid);
            saveMutes();
            return false;
        }
        return true;
    }

    public String getMuteReason(UUID uuid) {
        MuteData d = mutes.get(uuid);
        return d != null ? d.reason : null;
    }

    public String getMuter(UUID uuid) {
        MuteData d = mutes.get(uuid);
        return d != null ? d.muter : null;
    }

    public long getMuteEnd(UUID uuid) {
        MuteData d = mutes.get(uuid);
        return d != null ? d.end : -1;
    }

    private static class MuteData {
        final String reason;
        final String muter;
        final long end;
        MuteData(String reason, String muter, long end) { this.reason = reason; this.muter = muter; this.end = end; }
    }
}

package org.cyberluca.cyberBan.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cyberluca.cyberBan.CyberBanPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HistoryManager {

    private final CyberBanPlugin plugin;
    private final File file;
    private FileConfiguration cfg;

    public HistoryManager(CyberBanPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "history.yml");
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
    }

    public void saveHistory() {
        try { cfg.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public void addEntry(String spieler, String type, String actor, long bis, String grund) {
        if (cfg == null) load();
        String id = UUID.randomUUID().toString();
        String base = "history." + spieler + "." + id;
        cfg.set(base + ".time", System.currentTimeMillis());
        cfg.set(base + ".type", type);
        cfg.set(base + ".actor", actor);
        cfg.set(base + ".bis", bis);
        cfg.set(base + ".grund", grund);
        saveHistory();
    }

    public List<Map<String,Object>> getHistory(String spieler) {
        List<Map<String,Object>> out = new ArrayList<>();
        if (cfg == null) load();
        if (!cfg.contains("history." + spieler)) return out;
        for (String k : cfg.getConfigurationSection("history." + spieler).getKeys(false)) {
            Map<String,Object> m = cfg.getConfigurationSection("history." + spieler + "." + k).getValues(false);
            out.add(m);
        }
        out.sort((a,b) -> Long.compare((Long)b.get("time"), (Long)a.get("time")));
        return out;
    }
}

package org.cyberluca.cyberBan.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cyberluca.cyberBan.CyberBanPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class IPBanManager {

    private final CyberBanPlugin plugin;
    private final File file;
    private FileConfiguration cfg;
    private final Map<String, IPBanData> ipbans = new HashMap<>();

    public IPBanManager(CyberBanPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "ipbans.yml");
        load();
    }

    public void load() {
        try {
            plugin.getDataFolder().mkdirs();
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cfg = YamlConfiguration.loadConfiguration(file);
        if (cfg.contains("ipbans")) {
            for (String key : cfg.getConfigurationSection("ipbans").getKeys(false)) {
                long bis = cfg.getLong("ipbans." + key + ".bis", -1L);
                String grund = cfg.getString("ipbans." + key + ".grund", "IP-Bann");
                ipbans.put(key, new IPBanData(key, bis, grund));
            }
        }
    }

    public void saveIPBans() {
        if (cfg == null) cfg = YamlConfiguration.loadConfiguration(file);
        cfg.set("ipbans", null);
        for (IPBanData d : ipbans.values()) {
            cfg.set("ipbans." + d.getIp() + ".bis", d.getBis());
            cfg.set("ipbans." + d.getIp() + ".grund", d.getGrund());
        }
        try { cfg.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public void ipban(String ip, long bis, String grund) {
        IPBanData data = new IPBanData(ip, bis, grund);
        ipbans.put(ip, data);


        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p.getAddress() != null && p.getAddress().getAddress().getHostAddress().equals(ip)) {
                p.kickPlayer("Deine IP ist gebannt: " + grund);
            }
        });
        saveIPBans();

        if (bis > System.currentTimeMillis()) {
            long delayTicks = Math.max(1L, (bis - System.currentTimeMillis()) / 50L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                IPBanData b = ipbans.get(ip);
                if (b != null && b.getBis() == bis) {
                    ipbans.remove(ip);
                    saveIPBans();
                    plugin.getLogger().info("[CyberBan] IP automatisch entbannt: " + ip);
                }
            }, delayTicks);
        }
    }


    public void banIp(String ip, long bis, String grund) { ipban(ip, bis, grund); }

    public boolean isIPBanned(String ip) {
        IPBanData d = ipbans.get(ip);
        if (d == null) return false;
        if (d.getBis() == -1L) return true;
        if (d.getBis() < System.currentTimeMillis()) {
            ipbans.remove(ip);
            saveIPBans();
            return false;
        }
        return true;
    }

    public Optional<IPBanData> getIPBan(String ip) { return Optional.ofNullable(ipbans.get(ip)); }


    public static class IPBanData {
        private final String ip;
        private final long bis;
        private final String grund;
        public IPBanData(String ip, long bis, String grund) { this.ip = ip; this.bis = bis; this.grund = grund; }
        public String getIp() { return ip; }
        public long getBis() { return bis; }
        public String getGrund() { return grund; }
    }
}

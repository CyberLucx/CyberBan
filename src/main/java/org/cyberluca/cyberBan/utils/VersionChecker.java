package org.cyberluca.cyberBan.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.cyberluca.cyberBan.CyberBanPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VersionChecker {

    private final CyberBanPlugin plugin;
    private BukkitTask task;


    private static final String GITHUB_REPO = "CyberLucx/CyberBan";

    public VersionChecker(CyberBanPlugin plugin) {
        this.plugin = plugin;
    }


    public void start() {
        boolean enabled = plugin.getConfig().getBoolean("version-check.enabled", true);
        if (!enabled) return;

        int minutes = Math.max(1, plugin.getConfig().getInt("version-check.interval-minutes", 30));
        long ticks = Math.max(20L, minutes * 60L * 20L); // mindestens 1s (20 ticks)

        // sofort prüfen
        checkNow();

        // periodisch asynchron planen
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                checkNow();
            }
        }.runTaskTimerAsynchronously(plugin, ticks, ticks);
    }


    public void stop() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }


    public void checkNow() {
        new Thread(() -> {
            try {
                String latest = fetchLatestReleaseTagFromGitHub(GITHUB_REPO);
                if (latest == null || latest.isEmpty()) {
                    plugin.getLogger().fine("[VersionChecker] No remote version found for repo " + GITHUB_REPO);
                    return;
                }

                String current = plugin.getDescription().getVersion();
                if (isNewerVersion(latest, current)) {
                    notifyOperators(current, latest);
                    plugin.getLogger().info("[VersionChecker] New version: " + latest + " (current: " + current + ")");
                } else {
                    plugin.getLogger().fine("[VersionChecker] Plugin up-to-date (" + current + ")");
                }
            } catch (Exception ex) {
                plugin.getLogger().warning("[VersionChecker] Error during version check: " + ex.getMessage());
            }
        }, "CyberBan-VersionChecker").start();
    }


    private void notifyOperators(String current, String latest) {
        String msg = null;
        try {
            // MessageUtil.format erwartet map-based placeholders:
            msg = plugin.getMessageUtil().format("update-available",
                    Map.of("current", current, "latest", latest));
        } catch (Throwable t) {
            // falls MessageUtil fehlt oder anders signiert -> fallback message
            msg = "&c[CyberBan] Neue Version vorhanden: &f" + latest + " &c(you have " + current + ")";
        }

        String colored = ChatColor.translateAlternateColorCodes('&', msg);

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p.isOp() || p.hasPermission("cyberban.notify")) {
                p.sendMessage(colored);
            }
        });

        Bukkit.getConsoleSender().sendMessage(colored);
    }


    private String fetchLatestReleaseTagFromGitHub(String ownerRepo) {
        try {
            String api = "https://api.github.com/repos/" + ownerRepo + "/releases/latest";
            HttpURLConnection conn = (HttpURLConnection) URI.create(api).toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(6000);
            conn.setRequestProperty("User-Agent", "CyberBan-VersionChecker");
            int code = conn.getResponseCode();
            if (code != 200) {
                plugin.getLogger().fine("[VersionChecker] GitHub API returned " + code + " for " + api);
                return null;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append('\n');
                String json = sb.toString();

                // tag_name field parsen
                Pattern p = Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"");
                Matcher m = p.matcher(json);
                if (m.find()) return m.group(1).trim();

                // fallback: "name"
                p = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
                m = p.matcher(json);
                if (m.find()) return m.group(1).trim();
            }
        } catch (Exception e) {
            plugin.getLogger().fine("[VersionChecker] fetchLatestReleaseTagFromGitHub error: " + e.getMessage());
        }
        return null;
    }




    private boolean isNewerVersion(String remote, String local) {
        try {
            String r = remote.trim();
            if (r.startsWith("v") || r.startsWith("V")) r = r.substring(1);
            String l = local.trim();
            if (l.startsWith("v") || l.startsWith("V")) l = l.substring(1);

            String[] ra = r.split("[^0-9]+");
            String[] la = l.split("[^0-9]+");
            int len = Math.max(ra.length, la.length);
            for (int i = 0; i < len; i++) {
                int rn = (i < ra.length && !ra[i].isEmpty()) ? Integer.parseInt(ra[i]) : 0;
                int ln = (i < la.length && !la[i].isEmpty()) ? Integer.parseInt(la[i]) : 0;
                if (rn > ln) return true;
                if (rn < ln) return false;
            }
            return false;
        } catch (Exception e) {
            return !remote.equals(local);
        }
    }
}

package org.cyberluca.cyberBan.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.cyberluca.cyberBan.CyberBanPlugin;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final CyberBanPlugin plugin;

    public PlayerListener(CyberBanPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        if (plugin.getBanManager().isBanned(uuid)) {
            String reason = plugin.getBanManager().getBanReason(uuid);
            String banner = plugin.getBanManager().getBanner(uuid);
            long end = plugin.getBanManager().getBanEnd(uuid);
            String kick = plugin.getMessageUtil().formatKick(reason, banner, end);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kick);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (plugin.getMuteManager().isMuted(uuid)) {
            String reason = plugin.getMuteManager().getMuteReason(uuid);
            String muter = plugin.getMuteManager().getMuter(uuid);
            long end = plugin.getMuteManager().getMuteEnd(uuid);
            String msg = plugin.getMessageUtil().format("mute-notify",
                    java.util.Map.of("player", event.getPlayer().getName(), "muter", muter, "reason", reason, "duration", end==-1?"Permanent":plugin.getMessageUtil().formatTime(end - System.currentTimeMillis()))
            );
            event.getPlayer().sendMessage(msg);
            event.setCancelled(true);
        }
    }
}

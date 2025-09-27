package org.cyberluca.cyberBan;

import org.bukkit.plugin.java.JavaPlugin;
import org.cyberluca.cyberBan.commands.*;
import org.cyberluca.cyberBan.listeners.PlayerListener;
import org.cyberluca.cyberBan.manager.*;
import org.cyberluca.cyberBan.utils.MessageUtil;

public class CyberBanPlugin extends JavaPlugin {

    private BanManager banManager;
    private MuteManager muteManager;
    private IPBanManager ipBanManager;
    private HistoryManager historyManager;
    private MessageUtil messageUtil;

    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();

            saveResource("messages.yml", false);
        } catch (Exception ignored) {}


        this.messageUtil = new MessageUtil(this);
        this.banManager = new BanManager(this);
        this.muteManager = new MuteManager(this);
        this.ipBanManager = new IPBanManager(this);
        this.historyManager = new HistoryManager(this);


        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);


        if (getCommand("ban") != null) getCommand("ban").setExecutor(new BanCommand(this));
        if (getCommand("unban") != null) getCommand("unban").setExecutor(new UnbanCommand(this));
        if (getCommand("mute") != null) getCommand("mute").setExecutor(new MuteCommand(this));
        if (getCommand("unmute") != null) getCommand("unmute").setExecutor(new UnmuteCommand(this));
        if (getCommand("check") != null) getCommand("check").setExecutor(new CheckCommand(this));
        if (getCommand("ipban") != null) getCommand("ipban").setExecutor(new IPBanCommand(this));
        if (getCommand("softban") != null) getCommand("softban").setExecutor(new SoftBanCommand(this));
        if (getCommand("banhistory") != null) getCommand("banhistory").setExecutor(new BanHistoryCommand(this));
        if (getCommand("mutehistory") != null) getCommand("mutehistory").setExecutor(new MuteHistoryCommand(this));
        if (getCommand("cyberban") != null) getCommand("cyberban").setExecutor(new CyberBanCommand(this));

        getLogger().info("CyberBan v" + getDescription().getVersion() + " aktiviert.");
    }

    @Override
    public void onDisable() {
        if (banManager != null) banManager.saveBans();
        if (muteManager != null) muteManager.saveMutes();
        if (ipBanManager != null) ipBanManager.saveIPBans();
        if (historyManager != null) historyManager.saveHistory();

        getLogger().info("CyberBan deaktiviert.");
    }


    public BanManager getBanManager() { return banManager; }
    public MuteManager getMuteManager() { return muteManager; }
    public IPBanManager getIpBanManager() { return ipBanManager; }
    public HistoryManager getHistoryManager() { return historyManager; }
    public MessageUtil getMessageUtil() { return messageUtil; }
}

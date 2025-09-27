package org.cyberluca.cyberBan;

import org.bukkit.plugin.java.JavaPlugin;
import org.cyberluca.cyberBan.commands.*;
import org.cyberluca.cyberBan.listeners.PlayerListener;
import org.cyberluca.cyberBan.manager.*;
import org.cyberluca.cyberBan.utils.MessageUtil;
import org.cyberluca.cyberBan.utils.VersionManager;


public class CyberBanPlugin extends JavaPlugin {

    private static CyberBanPlugin instance;

    private BanManager banManager;
    private MuteManager muteManager;
    private IPBanManager ipBanManager;
    private HistoryManager historyManager;
    private MessageUtil messageUtil;
    private VersionManager versionManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Enabling CyberBan v" + getDescription().getVersion() + "...");

        try {

            saveDefaultConfig();
            try {
                saveResource("messages.yml", false);
            } catch (IllegalArgumentException ignored) {

            }


            this.messageUtil = new MessageUtil(this);


            this.banManager = new BanManager(this);
            this.muteManager = new MuteManager(this);
            this.ipBanManager = new IPBanManager(this);
            this.historyManager = new HistoryManager(this);


            this.versionManager = new VersionManager(this);

            this.versionManager.start();


            getServer().getPluginManager().registerEvents(new PlayerListener(this), this);


            if (getCommand("ban") != null) getCommand("ban").setExecutor(new BanCommand(this));
            if (getCommand("unban") != null) getCommand("unban").setExecutor(new UnbanCommand(this));
            if (getCommand("mute") != null) getCommand("mute").setExecutor(new MuteCommand(this));
            if (getCommand("unmute") != null) getCommand("unmute").setExecutor(new UnmuteCommand(this));
            if (getCommand("check") != null) getCommand("check").setExecutor(new CheckCommand(this));
            if (getCommand("banhistory") != null) getCommand("banhistory").setExecutor(new BanHistoryCommand(this));
            if (getCommand("mutehistory") != null) getCommand("mutehistory").setExecutor(new MuteHistoryCommand(this));
            if (getCommand("softban") != null) getCommand("softban").setExecutor(new SoftBanCommand(this));
            if (getCommand("ipban") != null) getCommand("ipban").setExecutor(new IPBanCommand(this));
            if (getCommand("cyberban") != null) getCommand("cyberban").setExecutor(new CyberBanCommand(this));

            if (getCommand("cyberbanversion") != null) getCommand("cyberbanversion").setExecutor(this.versionManager);

            getLogger().info("CyberBan enabled.");
        } catch (Exception e) {
            getLogger().severe("Error while enabling CyberBan:");
            e.printStackTrace();

            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling CyberBan...");

        try {
            // stop version manager first so no tasks remain
            if (versionManager != null) versionManager.stop();

            // save data from managers (implement save methods in managers)
            if (banManager != null) banManager.saveBans();
            if (muteManager != null) muteManager.saveMutes();
            if (ipBanManager != null) ipBanManager.saveIPBans();
            if (historyManager != null) historyManager.saveHistory();
        } catch (Exception e) {
            getLogger().severe("Error while disabling CyberBan:");
            e.printStackTrace();
        }

        getLogger().info("CyberBan disabled.");
    }

    // Getters
    public static CyberBanPlugin getInstance() {
        return instance;
    }

    public BanManager getBanManager() {
        return banManager;
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }

    public IPBanManager getIpBanManager() {
        return ipBanManager;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public VersionManager getVersionManager() {
        return versionManager;
    }
}

package org.cyberluca.cyberBan.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.cyberluca.cyberBan.CyberBanPlugin;


public class VersionManager implements CommandExecutor {

    private final CyberBanPlugin plugin;
    private final VersionChecker checker;

    public VersionManager(CyberBanPlugin plugin) {
        this.plugin = plugin;
        this.checker = new VersionChecker(plugin);

    }


    public void start() {
        checker.start();
    }


    public void stop() {
        checker.stop();
    }


    public void checkNow() {
        checker.checkNow();
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // nur "check" bedienen
        if (args.length == 0 || args[0].equalsIgnoreCase("check")) {
            sender.sendMessage(color("&eCyberBan: Checking for updates..."));


            new Thread(() -> {
                try {
                    checker.checkNow();
                    sender.sendMessage(color("&aCheck started - results will be sent to OPs/console if an update is available."));
                } catch (Throwable t) {
                    sender.sendMessage(color("&cError while performing version check: " + t.getMessage()));
                }
            }, "CyberBan-ManualVersionCheck").start();

            return true;
        }

        sender.sendMessage(color("&cUsage: /cyberbanversion check"));
        return true;
    }
}

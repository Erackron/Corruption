package com.mcdr.corruption.command;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.GlobalConfig;
import com.mcdr.corruption.util.CorAutoUpdater;
import com.mcdr.corruption.util.CorUpdateChecker;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UpdateCommand extends BaseCommand {
    public static void process() {
        if (!checkPermission("cor.update", true))
            return;

        if (args.length < 2) {
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.GRAY + "Usage: " + ChatColor.WHITE + "/" + label + " update <check/install>.");
            return;
        }
        String arg = args[1].toLowerCase();
        if (arg.equalsIgnoreCase("c") || arg.equalsIgnoreCase("check"))
            checkCommand();
        else if (arg.equalsIgnoreCase("i") || arg.equalsIgnoreCase("install"))
            installCommand();
    }

    private static void checkCommand() {
        if (CorUpdateChecker.checkForUpdate()) {
            String lastVer = CorUpdateChecker.getLastVersion();
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + "New version available, version " + ChatColor.GRAY + lastVer);
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + "To update, use " + ChatColor.GREEN + "/" + label + " update install");
        } else {
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + "No update needed, running the latest version (" + ChatColor.GRAY + Corruption.in.getDescription().getVersion() + ChatColor.WHITE + ")");
        }
    }

    private static void installCommand() {
        if (!CorUpdateChecker.checkForUpdate()) {
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + "No update needed, running the latest version (" + ChatColor.GRAY + Corruption.in.getDescription().getVersion() + ChatColor.WHITE + ")");
            return;
        }
        //If something went wrong, return. Errors will be handled in the CorUpdateChecker class
        if (CorUpdateChecker.timeStamp == -1)
            return;

        //Update the md5 hash for safety.
        //If something went wrong, return. Errors will be handled in the LabAutoUpdater class
        if (!CorAutoUpdater.updateMd5Hash())
            return;

        if (CorAutoUpdater.update()) {
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + "Updated successfully.");
            if (!GlobalConfig.reloadAfterUpdating && sender instanceof Player) {
                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + "Reload or restart your server for the changes to take effect.");
                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.RED + "WARNING: " + ChatColor.WHITE + "Don't use a pluginmanager to reload this plugin. This plugin is not responsible for the damage that may occur if you do that.");
            }
        } else {
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + "Update failed.");
            if (sender instanceof Player)
                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + "Check the console/logs for more information.");
        }
    }
}

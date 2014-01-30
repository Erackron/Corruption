package com.mcdr.corruption.command;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.util.Utility;
import org.bukkit.ChatColor;

public abstract class HelpCommand extends BaseCommand {
    public static void process() {
        if (!checkPermission("cor.help", true))
            return;

        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + "Commands list");
        if (Utility.hasPermission(sender, "cor.clear"))
            sender.sendMessage("/" + label + " clear: " + ChatColor.GRAY + "Clear informations given by /cor info.");
        if (Utility.hasPermission(sender, "cor.ignore"))
            sender.sendMessage("/" + label + " ignore: " + ChatColor.GRAY + "Toggle ignore state, which allows to not be affected by bosses.");
        if (Utility.hasPermission(sender, "cor.info"))
            sender.sendMessage("/" + label + " info: " + ChatColor.GRAY + "Display some global and non-lasting stats.");
        if (Utility.hasPermission(sender, "cor.list"))
            sender.sendMessage("/" + label + " list: " + ChatColor.GRAY + "Display the location of active bosses.");
        if (Utility.hasPermission(sender, "cor.loglevel"))
            sender.sendMessage("/" + label + " loglevel <new level>: " + ChatColor.GRAY + "View or change the current logging level.");
        if (Utility.hasPermission(sender, "cor.reload"))
            sender.sendMessage("/" + label + " reload: " + ChatColor.GRAY + "Reload configuration files.");
        if (Utility.hasPermission(sender, "cor.spawn")) {
            sender.sendMessage("/" + label + " spawn [type] <amount> <x> <y> <z> <World>: " + ChatColor.GRAY + "Spawn one or multiple bosses on the targeted block or specified coordinates.");
            sender.sendMessage("/" + label + " spawn [type] <amount> <player>:" + ChatColor.GRAY + "Spawn one or multiple bosses where the targeted player is looking.");
        }
        if (Utility.hasPermission(sender, "cor.stats"))
            sender.sendMessage("/" + label + " stats <player>: " + ChatColor.GRAY + "Display the leaderboard, or player stats.");
        if (Utility.hasPermission(sender, "cor.update"))
            sender.sendMessage("/" + label + " update <check/install>: " + ChatColor.GRAY + "Check for updates or install one if available.");
        if (Utility.hasPermission(sender, "cor.version"))
            sender.sendMessage("/" + label + " version: " + ChatColor.GRAY + "Check the version of " + Corruption.pluginName + " that is currently installed.");
        if (Utility.hasPermission(sender, "cor.viewer"))
            sender.sendMessage("/" + label + " viewer: " + ChatColor.GRAY + "Toggle viewer state, which allows to see boss healths.");
    }
}

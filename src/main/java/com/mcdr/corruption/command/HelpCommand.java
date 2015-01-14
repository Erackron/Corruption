package com.mcdr.corruption.command;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.permissions.Permissions;
import org.bukkit.ChatColor;

public abstract class HelpCommand extends BaseCommand {
    public static void process() {
        if (!checkPermission("cor.help", true))
            return;

        if (args.length > 1 && args[1].equalsIgnoreCase("region")) {
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Region commands list");
            if (Permissions.hasPermission(sender, "cor.spawn.region.list"))
                sender.sendMessage("/" + label + " spawn region list [world]: " + ChatColor.GRAY + "List existing spawners in a given World or globally.");
            if (Permissions.hasPermission(sender, "cor.spawn.region.toggle"))
                sender.sendMessage("/" + label + " spawn region enable [id|name]: " + ChatColor.GRAY + "Enable a spawner with given id/name or nearby.");
            if (Permissions.hasPermission(sender, "cor.spawn.region.toggle"))
                sender.sendMessage("/" + label + " spawn region disable [id|name]: " + ChatColor.GRAY + "Disable a spawner with given id/name or nearby.");
            if (Permissions.hasPermission(sender, "cor.spawn.region.info"))
                sender.sendMessage("/" + label + " spawn region info [id|name]: " + ChatColor.GRAY + "Gives the info of all spawners with name/id or nearby.");
            if (Permissions.hasPermission(sender, "cor.spawn.region.manage"))
                sender.sendMessage("/" + label + " spawn region add <name> [properties]: " + ChatColor.GRAY + "Create a new spawner.");
            if (Permissions.hasPermission(sender, "cor.spawn.region.manage"))
                sender.sendMessage("/" + label + " spawn region remove [id|name]: " + ChatColor.GRAY + "Remove a spawner with given id/name or nearby.");
            if (Permissions.hasPermission(sender, "cor.spawn.region.edit")) {
                sender.sendMessage("/" + label + " spawn region edit <id|name> <property> <value>: " + ChatColor.GRAY + "Edit a spawner property.");
                sender.sendMessage("/" + label + " spawn region edit <id|name> addBoss <bossName> <spawnChance>: " + ChatColor.GRAY + "Add a boss to the spawner.");
                sender.sendMessage("/" + label + " spawn region edit <id|name> removeBoss <bossName>" + ChatColor.GRAY + "Remove a boss from the spawner.");
                sender.sendMessage("/" + label + " spawn region edit <id|name> editBoss <bossName> chance:<spawnChance>: " + ChatColor.GRAY + "Edit a boss in the spawner.");
                sender.sendMessage("/" + label + " spawn region edit <id|name> editBoss <bossName> name:<bossName> [chance:<spawnChance>]: " + ChatColor.GRAY + "Edit a boss in the spawner.");
            }
            return;
        }

        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Commands list");
        if (Permissions.hasPermission(sender, "cor.clear"))
            sender.sendMessage("/" + label + " clear: " + ChatColor.GRAY + "Clear informations given by /cor info.");
        if (Permissions.hasPermission(sender, "cor.ignore"))
            sender.sendMessage("/" + label + " ignore: " + ChatColor.GRAY + "Toggle ignore state, which allows to not be affected by bosses.");
        if (Permissions.hasPermission(sender, "cor.info"))
            sender.sendMessage("/" + label + " info: " + ChatColor.GRAY + "Display some global and non-lasting stats.");
        if (Permissions.hasPermission(sender, "cor.list"))
            sender.sendMessage("/" + label + " list: " + ChatColor.GRAY + "Display the location of active bosses.");
        if (Permissions.hasPermission(sender, "cor.loglevel"))
            sender.sendMessage("/" + label + " loglevel <new level>: " + ChatColor.GRAY + "View or change the current logging level.");
        if (Permissions.hasPermission(sender, "cor.reload"))
            sender.sendMessage("/" + label + " reload: " + ChatColor.GRAY + "Reload configuration files.");

        String regionOptions = getAvailableRegionOptions();
        if (!regionOptions.equals("")) {
            sender.sendMessage("/" + label + " spawn region <" + regionOptions.substring(0, regionOptions.length() - 1) + "> : " + ChatColor.GRAY + "For more info, use " + ChatColor.GRAY + "/" + label + " help region" + ChatColor.WHITE + ".");
        }

        if (Permissions.hasPermission(sender, "cor.spawn")) {
            sender.sendMessage("/" + label + " spawn <type> [<amount> [[<x> <y> <z>] <World>]]]: " + ChatColor.GRAY + "Spawn one or multiple bosses on the targeted block or specified coordinates.");
            sender.sendMessage("/" + label + " spawn <type> <amount> <player>: " + ChatColor.GRAY + "Spawn one or multiple bosses where the targeted player is looking.");
        }
        if (Permissions.hasPermission(sender, "cor.stats"))
            sender.sendMessage("/" + label + " stats <player>: " + ChatColor.GRAY + "Display the leaderboard, or player stats.");
        if (Permissions.hasPermission(sender, "cor.update"))
            sender.sendMessage("/" + label + " update <check/install>: " + ChatColor.GRAY + "Check for updates or install one if available.");
        if (Permissions.hasPermission(sender, "cor.version"))
            sender.sendMessage("/" + label + " version: " + ChatColor.GRAY + "Check the version of " + Corruption.getPluginName() + " that is currently installed.");
        if (Permissions.hasPermission(sender, "cor.viewer"))
            sender.sendMessage("/" + label + " viewer: " + ChatColor.GRAY + "Toggle viewer state, which allows to see boss healths.");
    }

    private static String getAvailableRegionOptions() {
        String options = "";
        if (Permissions.hasPermission(sender, "cor.spawn.region.list")) {
            options += "list|";
        }
        if (Permissions.hasPermission(sender, "cor.spawn.region.info")) {
            options += "info|";
        }
        if (Permissions.hasPermission(sender, "cor.spawn.region.toggle")) {
            options += "enable|disable|";
        }
        if (Permissions.hasPermission(sender, "cor.spawn.region.manage")) {
            options += "add|remove|";
        }
        if (Permissions.hasPermission(sender, "cor.spawn.region.edit")) {
            options += "edit|";
        }
        return options;
    }
}

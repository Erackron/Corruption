package com.mcdr.corruption.command;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.permissions.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCommand {
    protected static boolean processed;
    protected static CommandSender sender;
    protected static String[] args;
    protected static String label;

    protected static boolean checkPermission(String permission, boolean consoleUsage) {
        processed = true;

        if (!consoleUsage && !(sender instanceof Player)) {
            sender.sendMessage("[" + Corruption.getPluginName() + "] This command doesn't support console usage.");
            return false;
        }

        if (!Permissions.hasPermission(sender, permission)) {
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "You don't have the permission for this command.");
            sender.sendMessage(ChatColor.GRAY + permission + ChatColor.WHITE + " is needed.");
            return false;
        }

        return true;
    }
}

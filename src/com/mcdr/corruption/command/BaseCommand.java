package com.mcdr.corruption.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.util.Utility;

public abstract class BaseCommand {
	protected static boolean processed;
	protected static CommandSender sender;
	protected static String[] args;
	protected static String label;
	
	protected static boolean checkPermission(String permission, boolean consoleUsage) {
		processed = true;
		
		if (!consoleUsage && !(sender instanceof Player)) {
			sender.sendMessage("["+Corruption.pluginName+"] This command doesn't support console usage.");
			return false;
		}
		
		if (!Utility.hasPermission(sender, permission)) {
			sender.sendMessage(ChatColor.GOLD + "["+Corruption.pluginName+"] " + ChatColor.WHITE + "You don't have the permission for this command.");
			sender.sendMessage(ChatColor.GRAY + permission + ChatColor.WHITE + " is needed.");
			return false;
		}
		
		return true;
	}
}

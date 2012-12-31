package com.mcdr.likeaboss.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcdr.likeaboss.Utility;

public abstract class BaseCommand {
	protected static boolean processed;
	protected static CommandSender sender;
	protected static String[] args;
	
	protected static boolean CheckPermission(String permission, boolean consoleUsage) {
		processed = true;
		
		if (!consoleUsage && !(sender instanceof Player)) {
			sender.sendMessage("[LAB] This command doesn't support console usage.");
			return false;
		}
		
		if (!Utility.hasPermission(sender, permission)) {
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "You don't have the permission for this command.");
			sender.sendMessage(ChatColor.GRAY + permission + ChatColor.WHITE + " is needed.");
			return false;
		}
		
		return true;
	}
}

package com.mcdr.corruption.command;

import org.bukkit.ChatColor;

import com.mcdr.corruption.Corruption;

public abstract class HelpCommand extends BaseCommand {
	public static void Process() {
		if (!checkPermission("cor.help", true))
			return;
		
		sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + "Commands list");
		sender.sendMessage("/" + label + " clear: " + ChatColor.GRAY + "Clear informations given by /lab info.");
		sender.sendMessage("/" + label + " ignore: " + ChatColor.GRAY + "Toggle ignore state, which allows to not be affected by bosses.");
		sender.sendMessage("/" + label + " info: " + ChatColor.GRAY + "Display some global and non-lasting stats.");
		sender.sendMessage("/" + label + " list: " + ChatColor.GRAY + "Display the location of active bosses.");
		sender.sendMessage("/" + label + " reload: " + ChatColor.GRAY + "Reload configuration files.");
		sender.sendMessage("/" + label + " spawn [type] <amount>: " + ChatColor.GRAY + "Spawn one or multiple bosses on the targeted block.");
		sender.sendMessage("/" + label + " stats <player>: " + ChatColor.GRAY + "Display the leaderboard, or player stats.");
		sender.sendMessage("/" + label + " update <check/install>: " + ChatColor.GRAY + "Check for updates or install one if available.");
		sender.sendMessage("/" + label + " viewer: " + ChatColor.GRAY + "Toggle viewer state, which allows to see boss healths.");
	}
}

package com.mcdr.likeaboss.command;

import org.bukkit.ChatColor;

import com.mcdr.likeaboss.stats.StatsManager;


public abstract class ClearCommand extends BaseCommand {
	public static void Process() {
		if (!CheckPermission("lab.clear", true))
			return;
		
		StatsManager.Clear();
		
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Cleared");
	}
}

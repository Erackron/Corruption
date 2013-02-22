package com.mcdr.corruption.command;

import org.bukkit.ChatColor;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.stats.StatsManager;


public abstract class ClearCommand extends BaseCommand {
	public static void process() {
		if (!checkPermission("cor.clear", true))
			return;
		
		StatsManager.Clear();
		
		sender.sendMessage(ChatColor.GOLD + "["+Corruption.pluginName+"] " + ChatColor.WHITE + "Cleared");
	}
}

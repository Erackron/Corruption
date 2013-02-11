package com.mcdr.corruption.command;

import org.bukkit.ChatColor;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.stats.StatsManager;


public abstract class ClearCommand extends BaseCommand {
	public static void Process() {
		if (!checkPermission("cor.clear", true))
			return;
		
		StatsManager.Clear();
		
		sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + "Cleared");
	}
}

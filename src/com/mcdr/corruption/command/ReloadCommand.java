package com.mcdr.corruption.command;

import org.bukkit.ChatColor;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.ConfigManager;
import com.mcdr.corruption.task.TaskManager;


public abstract class ReloadCommand extends BaseCommand {
	public static void process() {
		if (!checkPermission("cor.reload", true))
			return;
		
		ConfigManager.Load();
		TaskManager.restart();
		
		sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + "Reloaded");
	}
}

package com.mcdr.likeaboss.command;

import org.bukkit.ChatColor;

import com.mcdr.likeaboss.config.ConfigManager;
import com.mcdr.likeaboss.task.TaskManager;


public abstract class ReloadCommand extends BaseCommand {
	public static void Process() {
		if (!CheckPermission("lab.reload", true))
			return;
		
		ConfigManager.Load();
		TaskManager.Restart();
		
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Reloaded");
	}
}

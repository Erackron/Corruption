package com.mcdr.likeaboss.command;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.mcdr.likeaboss.entity.LabEntityManager;
import com.mcdr.likeaboss.stats.StatsManager;


public abstract class InfoCommand extends BaseCommand {
	public static void Process() {
		if (!CheckPermission("lab.info", true))
			return;
		
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Info");
		sender.sendMessage(ChatColor.GRAY + "Boss Killed: " + StatsManager.getBossesKilled());
		sender.sendMessage(ChatColor.GRAY + "Boss Count: " + LabEntityManager.getBosses().size());
			
		Map<Material, Integer> droped = StatsManager.getDroped();
		for (Entry<Material, Integer> entry : droped.entrySet())
			sender.sendMessage(ChatColor.GRAY + entry.getKey().toString() + " found: " + entry.getValue());
	}
}

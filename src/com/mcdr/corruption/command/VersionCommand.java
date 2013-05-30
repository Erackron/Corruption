package com.mcdr.corruption.command;

import org.bukkit.ChatColor;

import com.mcdr.corruption.Corruption;

public class VersionCommand extends BaseCommand{
	public static void process(){
		if(!checkPermission("cor.version", true))
			return;
		
		sender.sendMessage(ChatColor.GOLD + "["+Corruption.pluginName+"]" + ChatColor.WHITE + " Version "+Corruption.in.getDescription().getVersion());
	}
}

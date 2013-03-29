package com.mcdr.corruption.command;

import com.mcdr.corruption.Corruption;

public class VersionCommand extends BaseCommand{
	public static void process(){
		if(!checkPermission("cor.version", true))
			return;
		
		sender.sendMessage("["+Corruption.pluginName+"] Version "+Corruption.in.getDescription().getVersion());
	}
}

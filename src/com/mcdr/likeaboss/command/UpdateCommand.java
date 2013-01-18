package com.mcdr.likeaboss.command;

import org.bukkit.ChatColor;

import com.mcdr.likeaboss.Likeaboss;
import com.mcdr.likeaboss.util.LabAutoUpdater;
import com.mcdr.likeaboss.util.LabUpdateChecker;

public class UpdateCommand extends BaseCommand {
	public static void Process(){
		if (!CheckPermission("lab.update", true))
			return;
		
		if(args.length < 2){
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.GRAY + "Usage: " + ChatColor.WHITE + "/lab update <check/install>.");
			return;
		}
		
		switch(args[1].toLowerCase()){
			case "c":
			case "check":
				checkCommand();
				break;
			case "i":
			case "install":
				installCommand();
				break;
			default:
		}
		return;
	}
	
	private static void checkCommand(){
		if(LabUpdateChecker.updateNeeded()){
			String lastVer = LabUpdateChecker.getLastVersion();
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "New version available, version " + ChatColor.GRAY +lastVer);
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "To update, use " + ChatColor.GREEN + "/lab update install");
		} else {
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "No update needed, running the latest version (" + ChatColor.GRAY + Likeaboss.in.getDescription().getVersion() + ChatColor.WHITE + ")");
		}
	}
	
	private static void installCommand(){
		if(!LabUpdateChecker.updateNeeded()){
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "No update needed, running the latest version (" + ChatColor.GRAY + Likeaboss.in.getDescription().getVersion() + ChatColor.WHITE + ")");
			return;
		}
		//If something went wrong, return. Errors will be handled in the LabUpdateChecker class
		if(LabUpdateChecker.timeStamp==-1)
			return;
		
		long timeStamp = LabAutoUpdater.timeStamp;
		//Get the download link if not previously done or if it was done more than half an hour ago (1000*60*30 ms)
		if(timeStamp==-1 || System.currentTimeMillis()-timeStamp>1000*60*30)
			LabAutoUpdater.getDownloadUrl();
		
		//If something went wrong, return. Errors will be handled in the LabAutoUpdater class
		if(LabAutoUpdater.timeStamp==-1)
			return;
		
		if(LabAutoUpdater.update())
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Updated successfully.");
		else {
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Update failed.");
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Check the console/logs for more information.");
		}
	}
}

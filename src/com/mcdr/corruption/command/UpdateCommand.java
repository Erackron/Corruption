package com.mcdr.corruption.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.util.CorAutoUpdater;
import com.mcdr.corruption.util.CorUpdateChecker;

public class UpdateCommand extends BaseCommand {
	public static void Process(){
		if (!checkPermission("cor.update", true))
			return;
		
		if(args.length < 2){
			sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.GRAY + "Usage: " + ChatColor.WHITE + "/lab update <check/install>.");
			return;
		}
		String arg = args[1].toLowerCase();
		if(arg.equalsIgnoreCase("c")||arg.equalsIgnoreCase("check"))
			checkCommand();
		else if(arg.equalsIgnoreCase("i")||arg.equalsIgnoreCase("install"))
			installCommand();
		return;
	}
	
	private static void checkCommand(){
		if(CorUpdateChecker.updateNeeded()){
			String lastVer = CorUpdateChecker.getLastVersion();
			sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + "New version available, version " + ChatColor.GRAY +lastVer);
			sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + "To update, use " + ChatColor.GREEN + "/" + label + " update install");
		} else {
			sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + "No update needed, running the latest version (" + ChatColor.GRAY + Corruption.in.getDescription().getVersion() + ChatColor.WHITE + ")");
		}
	}
	
	private static void installCommand(){
		if(!CorUpdateChecker.updateNeeded()){
			sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + "No update needed, running the latest version (" + ChatColor.GRAY + Corruption.in.getDescription().getVersion() + ChatColor.WHITE + ")");
			return;
		}
		//If something went wrong, return. Errors will be handled in the CorUpdateChecker class
		if(CorUpdateChecker.timeStamp==-1)
			return;
		
		//Update the md5 hash for safety.
		//If something went wrong, return. Errors will be handled in the LabAutoUpdater class
		if(!CorAutoUpdater.updateMd5Hash())
			return;
		
		if(CorAutoUpdater.update())
			sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + "Updated successfully.");
		else {
			sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + "Update failed.");
			if(sender instanceof Player)
				sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + "Check the console/logs for more information.");
		}
	}
}

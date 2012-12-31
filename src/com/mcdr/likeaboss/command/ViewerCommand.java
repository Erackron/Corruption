package com.mcdr.likeaboss.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mcdr.likeaboss.player.LabPlayer;
import com.mcdr.likeaboss.player.LabPlayerData;
import com.mcdr.likeaboss.player.LabPlayerManager;


public abstract class ViewerCommand extends BaseCommand {
	public static void Process() {
		if (!CheckPermission("lab.viewer", false))
			return;
		
		LabPlayer labPlayer = LabPlayerManager.getLabPlayer((Player) sender);
		
		if (labPlayer == null) {
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Oops, something went wrong.");
			sender.sendMessage("Please notify the plugin author.");
		}
		
		else {
			LabPlayerData labPlayerData = labPlayer.getLabPlayerData();
			boolean viewer = labPlayerData.getViewer();
			
			labPlayerData.setViewer(!viewer);
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Viewer: " + ChatColor.GREEN + !viewer);
		}
	}
}

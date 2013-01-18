package com.mcdr.likeaboss.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mcdr.likeaboss.player.LabPlayerManager;
import com.mcdr.likeaboss.util.LabUpdateChecker;
import com.mcdr.likeaboss.util.Utility;


public class LabPlayerListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if(LabUpdateChecker.updateNeeded() && Utility.hasPermission(player, "lab.update")){
			player.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + " New version available, version " + LabUpdateChecker.lastVer);
			player.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + " To update, use " + ChatColor.GREEN + "/lab update install");
		}
		LabPlayerManager.AddLabPlayer(player);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		LabPlayerManager.RemoveLabPlayer(event.getPlayer());
	}
}

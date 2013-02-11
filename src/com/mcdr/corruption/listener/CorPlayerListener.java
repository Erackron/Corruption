package com.mcdr.corruption.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.player.CorPlayerManager;
import com.mcdr.corruption.util.CorUpdateChecker;
import com.mcdr.corruption.util.Utility;


public class CorPlayerListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if(CorUpdateChecker.updateNeeded() && Utility.hasPermission(player, "lab.update")){
			player.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + " New version available, version " + CorUpdateChecker.lastVer);
			player.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + " To update, use " + ChatColor.GREEN + "/lab update install");
		}
		CorPlayerManager.addCorPlayer(player);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		CorPlayerManager.removeCorPlayer(event.getPlayer());
	}
}

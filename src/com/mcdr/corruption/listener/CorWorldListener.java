package com.mcdr.corruption.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.mcdr.corruption.config.WorldConfig;


public class CorWorldListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldLoad(WorldLoadEvent event) throws Exception {
		WorldConfig.Load(event.getWorld());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldUnload(WorldUnloadEvent event) {
		WorldConfig.Remove(event.getWorld());
	}
}

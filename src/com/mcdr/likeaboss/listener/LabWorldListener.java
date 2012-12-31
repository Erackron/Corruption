<<<<<<< HEAD:src/com/mcdr/likeaboss/listener/LabWorldListener.java
package com.mcdr.likeaboss.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.mcdr.likeaboss.config.WorldConfig;


public class LabWorldListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldLoad(WorldLoadEvent event) throws Exception {
		WorldConfig.Load(event.getWorld());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldUnload(WorldUnloadEvent event) {
		WorldConfig.Remove(event.getWorld());
	}
}
=======
package com.mcdr.likeaboss.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.mcdr.likeaboss.config.WorldConfig;


public class LabWorldListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldLoad(WorldLoadEvent event) throws Exception {
		WorldConfig.Load(event.getWorld());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldUnload(WorldUnloadEvent event) {
		WorldConfig.Remove(event.getWorld());
	}
}
>>>>>>> origin/EquipmentExpansion:src/com/mcdr/likeaboss/listener/LabWorldListener.java

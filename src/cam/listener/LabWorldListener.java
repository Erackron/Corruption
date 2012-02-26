package cam.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import cam.Likeaboss;
import cam.config.LabConfig;

public class LabWorldListener implements Listener {
	
	LabConfig labConfig = null;
	
	public LabWorldListener(Likeaboss plugin) {
		labConfig = plugin.getLabConfig();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldLoad(WorldLoadEvent event) throws Exception {
		labConfig.LoadWorldConfigFile(event.getWorld());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldUnload(WorldUnloadEvent event) {
		labConfig.RemoveWorldData(event.getWorld());
	}
}

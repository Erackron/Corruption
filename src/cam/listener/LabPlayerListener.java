package cam.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import cam.Likeaboss;
import cam.player.LabPlayerManager;

public class LabPlayerListener implements Listener {
	
	LabPlayerManager labPlayerManager = null;
	
	public LabPlayerListener(Likeaboss plugin) {
		labPlayerManager = plugin.getLabPlayerManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		labPlayerManager.AddLabPlayer(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		labPlayerManager.RemoveLabPlayer(event.getPlayer());
	}
}

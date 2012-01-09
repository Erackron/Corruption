package cam.listener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

import cam.Likeaboss;
import cam.player.LabPlayerManager;

public class LabPlayerListener extends PlayerListener {
	
	LabPlayerManager labPlayerManager = null;
	
	public LabPlayerListener(Likeaboss plugin) {
		labPlayerManager = plugin.getLabPlayerManager();
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		labPlayerManager.AddLabPlayer(event.getPlayer());
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		labPlayerManager.RemoveLabPlayer(event.getPlayer());
	}
}

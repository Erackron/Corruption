package cam.listener;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

import cam.player.LabPlayerManager;

public class LabPlayerListener extends PlayerListener {
	
	LabPlayerManager labPlayerManager = null;
	
	public LabPlayerListener(LabPlayerManager labPlayerManager) {
		this.labPlayerManager = labPlayerManager;
	}
	
	public void onPlayerQuit(PlayerQuitEvent event) {
		labPlayerManager.RemoveLabPlayer(event.getPlayer());
	}
}

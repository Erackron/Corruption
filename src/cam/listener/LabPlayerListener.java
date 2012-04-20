package cam.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import cam.Likeaboss;
import cam.config.GlobalConfig;
import cam.player.LabPlayer;
import cam.player.LabPlayerData;
import cam.player.LabPlayerManager;

public class LabPlayerListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String motd = GlobalConfig.MessageParam.MOTD.getMessage();
		LabPlayer labPlayer = LabPlayerManager.AddLabPlayer(player);
		LabPlayerData labPlayerData = labPlayer.getLabPlayerData();
		
		if (motd.length() > 0 && labPlayerData.getMotdReceived() == false) {
			player.sendMessage(motd.replace("{VERSION}", Likeaboss.instance.getDescription().getVersion()).replace('&', ChatColor.COLOR_CHAR));
			labPlayer.getLabPlayerData().setMotdReceived(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		LabPlayerManager.RemoveLabPlayer(event.getPlayer());
	}
}

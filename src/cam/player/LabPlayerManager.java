package cam.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import cam.Likeaboss;

public class LabPlayerManager {

	private static List<LabPlayer> labPlayers = new ArrayList<LabPlayer>();
	
	public LabPlayerManager() {
	}
	
	public void AddOnlinePlayers(Likeaboss plugin) {
		Object[] players = plugin.getServer().getOnlinePlayers();
		
		for (Object player : players)
			AddLabPlayer((Player) player);
	}
	
	public void AddLabPlayer(Player player) {
		labPlayers.add(new LabPlayer(player));
	}
	
	public void RemoveLabPlayer(Player player) {
		for (LabPlayer labPlayer : labPlayers) {
			if (labPlayer.getPlayer() == player) {
				labPlayers.remove(labPlayer);
				break;
			}
		}
	}

	public boolean IsLabPlayer(Player player) {
		for (LabPlayer labPlayer : labPlayers) {
			if (labPlayer.getPlayer() == player)
				return true;
		}
		return false;
	}

	public LabPlayer getLabPlayer(Player player) {
		for (LabPlayer labPlayer : labPlayers) {
			if (labPlayer.getPlayer() == player)
				return labPlayer;
		}
		return null; //Should never happen
	}
	
	public List<LabPlayer> getLabPlayers() {
		return labPlayers;
	}
}

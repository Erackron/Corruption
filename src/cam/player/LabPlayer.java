package cam.player;

import org.bukkit.entity.Player;

public class LabPlayer {

	private Player player;
	private boolean viewer;

	public LabPlayer(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean getViewer() {
		return viewer;
	}
	
	public void setViewer(boolean viewer) {
		this.viewer = viewer;
	}
}

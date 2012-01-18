package cam.player;

import org.bukkit.entity.Player;

public class LabPlayer {

	private Player player = null;
	private boolean viewer = false;
	//For boss proximity
	private int lastTimeNotified = 0;
	private boolean warmingUp = false;
	private int warmingUpStartTime = 0;

	public LabPlayer(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean getViewer() {
		return viewer;
	}
	
	public int getLastTimeNotified() {
		return lastTimeNotified;
	}
		
	public boolean getWarmingUp() {
		return warmingUp;
	}
	
	public int getWarmingUpStartTime() {
		return warmingUpStartTime;
	}
	
	public void setViewer(boolean viewer) {
		this.viewer = viewer;
	}

	public void setLastTimeNotified(int lastTimeNotified) {
		this.lastTimeNotified = lastTimeNotified;
	}
	
	public void setWarmingUp(boolean warmingUp) {
		this.warmingUp = warmingUp;
	}

	public void setWarmingUpStartTime(int warmingUpStartTime) {
		this.warmingUpStartTime = warmingUpStartTime;
	}
}

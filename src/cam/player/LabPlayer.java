package cam.player;

import org.bukkit.entity.Player;

public class LabPlayer {

	private Player player = null;
	private LabPlayerData labPlayerData = null;
	private int ignoreTaskId = 0;
	//For boss proximity
	private int lastTimeNotified = 0;
	private boolean warmingUp = false;
	private int warmingUpStartTime = 0;
	
	public LabPlayer(Player player) {
		this.player = player;
		this.labPlayerData = new LabPlayerData();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public LabPlayerData getLabPlayerData() {
		return labPlayerData;
	}
	
	public int getIgnoreTaskId() {
		return ignoreTaskId;
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
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void setLabPlayerData(LabPlayerData labPlayerData) {
		this.labPlayerData = labPlayerData;
	}
	
	public void setIgnoreTaskId(int ignoreTaskId) {
		this.ignoreTaskId = ignoreTaskId;
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

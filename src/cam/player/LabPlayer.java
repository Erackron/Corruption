package cam.player;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class LabPlayer {
	private OfflinePlayer player; //Weird class naming indeed
	private LabPlayerData labPlayerData;
	private Map<String, Integer> bossesKilled = new HashMap<String, Integer>();
	private int ignoreTaskId;
	//For boss proximity
	private int lastTimeNotified;
	private boolean warmingUp;
	private int warmingUpStartTime;
	
	public LabPlayer(OfflinePlayer player) {
		this.player = player;
		this.labPlayerData = new LabPlayerData();
	}
	
	public void AddBossKilled(String string, int amount) {
		int baseAmount = 0;
		
		if (bossesKilled.containsKey(string))
			baseAmount = bossesKilled.get(string);
		
		bossesKilled.put(string, baseAmount + amount);
	}
	
	public Player getPlayer() {
		return player.getPlayer();
	}
	
	public String getName() {
		return player.getName();
	}
	
	public LabPlayerData getLabPlayerData() {
		return labPlayerData;
	}
	
	public Map<String, Integer> getBossesKilled() {
		return bossesKilled;
	}
	
	public int getTotalBossesKilled() {
		int amount = 0;
		
		for (Integer bossKilled : bossesKilled.values())
			amount += bossKilled;
		
		return amount;
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

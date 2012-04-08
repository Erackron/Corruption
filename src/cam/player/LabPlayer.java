package cam.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class LabPlayer {
	
	private OfflinePlayer player; //Weird class naming indeed
	private LabPlayerData labPlayerData;
	private Map<EntityType, Integer> bossesKilled = new HashMap<EntityType, Integer>();
	private int ignoreTaskId = 0;
	//For boss proximity
	private int lastTimeNotified = 0;
	private boolean warmingUp = false;
	private int warmingUpStartTime = 0;
	
	public LabPlayer(OfflinePlayer player) {
		this.player = player;
		this.labPlayerData = new LabPlayerData();
	}
	
	public void AddBossKilled(EntityType entityType, int amount) {
		int baseAmount = 0;
		if (bossesKilled.containsKey(entityType))
			baseAmount = bossesKilled.get(entityType);
		bossesKilled.put(entityType, baseAmount + amount);
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
	
	public Map<EntityType, Integer> getBossesKilled() {
		return bossesKilled;
	}
	
	public int getTotalBossesKilled() {
		int amount = 0;
		for (Entry<EntityType, Integer> bossKilled : bossesKilled.entrySet())
			amount += bossKilled.getValue();
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

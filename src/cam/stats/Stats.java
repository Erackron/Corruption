package cam.stats;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public class Stats {
	
	private int totalBossesKilled = 0;
	private Map<Material, Integer> droped = new HashMap<Material, Integer>();
	
	public void AddBossKilled(int amount) {
		totalBossesKilled += amount;
	}
	
	public void AddDrops(Material material, int amount) {
		int value = 0;
		if (droped.containsKey(material))
			value = droped.get(material);
		droped.put(material, value + amount);
	}
	
	public void Clear() {
		totalBossesKilled = 0;
		droped.clear();
	}
	
	public int getTotalBossesKilled() {
		return totalBossesKilled;
	}
	
	public Map<Material, Integer> getDroped() {
		return droped;
	}
}

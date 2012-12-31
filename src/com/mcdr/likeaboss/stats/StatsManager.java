package com.mcdr.likeaboss.stats;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public class StatsManager {
	private static int bossesKilled;
	private static Map<Material, Integer> droped = new HashMap<Material, Integer>();
	
	public static void AddBossKilled(int amount) {
		bossesKilled += amount;
	}
	
	public static void AddDrops(Material material, int amount) {
		int value = 0;
		if (droped.containsKey(material))
			value = droped.get(material);
		droped.put(material, value + amount);
	}
	
	public static void Clear() {
		bossesKilled = 0;
		droped.clear();
	}
	
	public static int getBossesKilled() {
		return bossesKilled;
	}
	
	public static Map<Material, Integer> getDroped() {
		return droped;
	}
}

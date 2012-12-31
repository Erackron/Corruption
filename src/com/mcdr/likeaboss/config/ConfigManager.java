<<<<<<< HEAD:src/com/mcdr/likeaboss/config/ConfigManager.java
package com.mcdr.likeaboss.config;

import org.bukkit.World;

import com.mcdr.likeaboss.Likeaboss;


public abstract class ConfigManager {
	public static void Load() {
		GlobalConfig.Load();
		AbilityConfig.Load();
		BossConfig.Load();
		
		if(Likeaboss.msInstalled){
			MagicSpellsConfig.Load();
		}
		
		for (World world : Likeaboss.in.getServer().getWorlds()) {
			WorldConfig.Load(world);
		}
	}
}
=======
package com.mcdr.likeaboss.config;

import org.bukkit.World;

import com.mcdr.likeaboss.Likeaboss;


public abstract class ConfigManager {
	public static void Load() {
		GlobalConfig.Load();
		AbilityConfig.Load();
		EquipmentConfig.Load();
		BossConfig.Load();
		
		if(Likeaboss.msInstalled){
			MagicSpellsConfig.Load();
		}
		
		for (World world : Likeaboss.in.getServer().getWorlds()) {
			WorldConfig.Load(world);
		}
	}
}
>>>>>>> origin/EquipmentExpansion:src/com/mcdr/likeaboss/config/ConfigManager.java

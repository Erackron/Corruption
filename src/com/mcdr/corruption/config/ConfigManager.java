package com.mcdr.corruption.config;

import org.bukkit.World;

import com.mcdr.corruption.Corruption;


public abstract class ConfigManager {
	public static void Load() {
		GlobalConfig.load();
		AbilityConfig.Load();
        ItemConfig.Load();
		// EquipmentConfig must be loaded before the BossConfig (for obvious reasons)
		EquipmentConfig.Load();
		BossConfig.Load();
		
		if(Corruption.msInstalled){
			MagicSpellsConfig.Load();
		}
		
		WorldConfig.resetWorldsData();
		for (World world : Corruption.in.getServer().getWorlds()) {
			WorldConfig.Load(world);
		}
	}
}

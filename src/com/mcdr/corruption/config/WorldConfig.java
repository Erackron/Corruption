package com.mcdr.corruption.config;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.mcdr.corruption.entity.CorItem;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mcdr.corruption.ability.Ability;
import com.mcdr.corruption.drop.Drop;
import com.mcdr.corruption.drop.Roll;
import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.util.CorLogger;
import com.mcdr.corruption.util.legacy.ItemNames;
import com.mcdr.corruption.world.WorldData;


public abstract class WorldConfig extends BaseConfig {
	private static Map<World, WorldData> worldsData = new HashMap<World, WorldData>();
		
	public static void Load(World world) {
		String worldName = world.getName();
		File file = new File(DATAFOLDER + SEPERATOR + "Worlds", worldName + ".yml");
		
		if (!file.exists())
			copyResource(file, "com/mcdr/corruption/config/world.yml");
		
		YamlConfiguration yamlConfig = loadConfig(file);
		WorldData worldData = new WorldData();
		
		LoadBosses(worldData, yamlConfig.getStringList("Boss"), worldName);
		LoadAbilities(worldData, yamlConfig.getStringList("Ability"), worldName);
		LoadLoots(worldData, yamlConfig.getConfigurationSection("Loot"), worldName);
		worldsData.put(world, worldData);
	}
	
	private static void LoadBosses(WorldData worldData, List<String> bossNames, String worldName) {
		Map<String, BossData> bossesData = BossConfig.getBossesData();
		
		for (String bossName : bossNames) {
			if (!bossesData.containsKey(bossName)) {
				CorLogger.w("'" + bossName + "' in '" + worldName + "' config file isn't a valid boss.");
				continue;
			}
			
			worldData.AddBossData(bossesData.get(bossName));
		}
	}
	
	private static void LoadAbilities(WorldData worldData, List<String> abilityNames, String worldName) {
		Map<String, Ability> abilities = AbilityConfig.getAbilities();
		
		for (String abilityName : abilityNames) {
			if (!abilities.containsKey(abilityName)) {
				CorLogger.w("'" + abilityName + "' in '" + worldName + " config file isn't a valid ability.");
				continue;
			}
			
			worldData.AddAbility(abilities.get(abilityName));
		}
	}
	
	private static void LoadLoots(WorldData worldData, ConfigurationSection lootSection, String worldName) {
		if (lootSection == null) {
			CorLogger.w("'Loot' in '" + worldName + "' config file is invalid.");
			return;
		}
		
		Set<String> rollStrings = lootSection.getKeys(false);
		
		for (String rollString : rollStrings) {
			ConfigurationSection rollSection = lootSection.getConfigurationSection(rollString);
			
			if (rollSection == null) {
				CorLogger.w("'Loot." + rollString + "' in '" + worldName + "' config file is invalid.");
				continue;
			}
			
			Roll roll = new Roll();
            Set<String> drops = rollSection.getKeys(false);
            for(String drop:drops){
                if(!rollSection.isConfigurationSection(drop)){
                    CorLogger.w("'"+rollSection.getCurrentPath()+"."+drop+"' in the " + worldName + " config file is invalid");
                    continue;
                }
                CorItem item = ItemConfig.items.get(drop);

                int probability = rollSection.getConfigurationSection(drop).getInt("Probability");
                int minQuantity = rollSection.getConfigurationSection(drop).getInt("MinQuantity");
                int maxQuantity = rollSection.getConfigurationSection(drop).getInt("MaxQuantity");

                if(item==null){
                    CorLogger.w("'"+rollSection.getCurrentPath()+"."+drop+"' in the " + worldName + " config file is invalid");
                    continue;
                }

                if(probability<0||probability>100){
                    CorLogger.w("'"+rollSection.getCurrentPath()+"."+drop+".Probability' in the " + worldName + " config file is invalid");
                    continue;
                }

                if(minQuantity<0){
                    CorLogger.w("'"+rollSection.getCurrentPath()+"."+drop+".MinQuantity' in the " + worldName + " config file is invalid");
                    continue;
                }
                if(maxQuantity<0){
                    CorLogger.w("'"+rollSection.getCurrentPath()+"."+drop+".MaxQuantity' in the " + worldName + " config file is invalid");
                    continue;
                }
                if(minQuantity>maxQuantity){
                    CorLogger.w("'"+rollSection.getCurrentPath()+"."+drop+".MaxQuantity' in the " + worldName + " config file is invalid");
                    continue;
                }

                roll.addDrop(new Drop(item, probability, minQuantity, maxQuantity));
            }
			
			worldData.AddRoll(roll);
		}
	}
	
	public static void resetWorldsData(){
		worldsData = new HashMap<World, WorldData>();
	}
	
	public static WorldData getWorldData(World world) {
		return worldsData.get(world);
	}
	
	public static void Remove(World world) {
		worldsData.remove(world);
	}
}

package com.mcdr.likeaboss.config;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import com.mcdr.likeaboss.Likeaboss;
import com.mcdr.likeaboss.ability.Ability;
import com.mcdr.likeaboss.drop.Drop;
import com.mcdr.likeaboss.drop.Roll;
import com.mcdr.likeaboss.entity.BossData;


public class BossConfig extends BaseConfig {
	private static Map<String, BossData> bossesData = new HashMap<String, BossData>();
	
	public static void Load() {
		File file = LoadFile("plugins/Likeaboss/bosses.yml", "cam/config/bosses.yml");
		
		if (file == null)
			return;
		
		YamlConfiguration yamlConfig = LoadConfig(file);
		
		LoadBosses(yamlConfig);
	}
	
	private static void LoadBosses(YamlConfiguration yamlConfig) {
		Set<String> bossNames = yamlConfig.getKeys(false);

		for (String bossName : bossNames) {
			ConfigurationSection configurationSection = yamlConfig.getConfigurationSection(bossName);
			
			if (configurationSection == null) {
				Likeaboss.l.warning("[Likeaboss] '" + bossName + "' in bosses config file is invalid.");
				continue;
			}
			
			String entityTypeString = yamlConfig.getString(bossName + ".EntityType");
			EntityType entityType = EntityType.fromName(entityTypeString);
			
			if (entityType == null) {
				Likeaboss.l.warning("[Likeaboss] '" + entityTypeString + "' in bosses config file isn't a valid EntityType.");
				continue;
			}
			
			BossData bossData = new BossData(bossName, entityType);

			if (!LoadSpawnValues(bossData, yamlConfig.getString(bossName + ".Spawn"), bossName))
				continue;
			if (!LoadStats(bossData, yamlConfig.getString(bossName + ".Stats"), bossName))
				continue;
			LoadAbilities(bossData, yamlConfig.getStringList(bossName + ".Ability"), bossName);
			LoadLoots(bossData, yamlConfig.getConfigurationSection(bossName + ".Loot"), bossName);
			loadEquipment(bossData, yamlConfig.getConfigurationSection(bossName + ".Equipment"), bossName);
			
			String [] bossNameS = bossName.split("_");
			bossName = bossNameS[0];
			for(int i = 1;i<bossNameS.length;i++){
				bossName += " "+bossNameS[i]; 
			}
			bossesData.put(bossName, bossData);
		}
	}
	
	private static boolean LoadSpawnValues(BossData bossData, String spawnString, String bossName) {
		if (spawnString == null) {
			Likeaboss.l.warning("[Likeaboss] '" + bossName + ".Spawn' in bosses config file is missing.");
			return false;
		}
		
//		if (!IsValidString(spawnString)) {
//			Likeaboss.logger.warning("[Likeaboss] Invalid values for '" + node + ".Spawn' in '" + worldName + "' config file");
//			continue;
//		}
		
		String[] spawnValues = spawnString.split(" ");
		
		if (spawnValues.length < 2) {
			Likeaboss.l.warning("[Likeaboss] Missing values for '" + bossName + ".Spawn' in bosses config file");
			return false;
		} else if (spawnValues.length < 3){
			String[] temp = new String[spawnValues.length + 1];
		    System.arraycopy(spawnValues, 0, temp, 0, spawnValues.length);
		    temp[spawnValues.length] = "256";
		    spawnValues = temp;
		}
	
		bossData.setSpawnData(Double.valueOf(spawnValues[0]), Double.valueOf(spawnValues[1]), Double.valueOf(spawnValues[2]));
		return true;
	}
	
	private static boolean LoadStats(BossData bossData, String statsString, String bossName) {
		if (statsString == null) {
			Likeaboss.l.warning("[Likeaboss] '" + bossName + ".Stats' in bosses config file is missing.");
			return false;
		}
		
//		if (!IsValidString(statsString)) {
//			Likeaboss.logger.warning("[Likeaboss] Invalid values for '" + node + ".Stats' in '" + worldName + "' config file");
//			continue;
//		}
		
		String[] statsValues = statsString.split(" ");
		
		if (statsValues.length < 3) {
			Likeaboss.l.warning("[Likeaboss] Missing values for '" + bossName + ".Stats' in bosses config file");
			return false;
		}
		
		bossData.setStatData(Double.valueOf(statsValues[0]), Double.valueOf(statsValues[1]), Double.valueOf(statsValues[2]));
		return true;
	}
	
	private static void LoadAbilities(BossData bossData, List<String> abilityNames, String bossName) {
		Map<String, Ability> abilities = AbilityConfig.getAbilities();
		
		for (String abilityName : abilityNames) {
			if (!abilities.containsKey(abilityName)) {
				Likeaboss.l.warning("[Likeaboss] '" + bossName + ".Ability." + abilityName + "' in bosses config file isn't a valid ability.");
				continue;
			}
			
			bossData.AddAbility(abilities.get(abilityName));
		}
	}
	
	private static void LoadLoots(BossData bossData, ConfigurationSection lootSection, String bossName) {
		if (lootSection == null) {
			Likeaboss.l.warning("[Likeaboss] '" + bossName + ".Loot" + "' in bosses config file is invalid.");
			return;
		}
		
		Set<String> rollStrings = lootSection.getKeys(false);
		
		for (String rollString : rollStrings) {
			ConfigurationSection rollSection = lootSection.getConfigurationSection(rollString);
			
			if (rollSection == null) {
				Likeaboss.l.warning("[Likeaboss] '" + bossName + ".Loot." + rollString + "' in bosses config file is invalid.");
				continue;
			}
			
			Roll roll = new Roll();
			Map<String, Object> drops = rollSection.getValues(false);
			
			for (Entry<String, Object> dropEntry : drops.entrySet()) {
				String dropString = dropEntry.getValue().toString();
				
//				if (!IsValidString(rawValue)) {
//					Likeaboss.logger.warning("[Likeaboss] Invalid values for '" + dropEntry + "' in '" + world.getName() + "' config file");
//					continue;
//				}
				
				String[] dropValues = dropString.split(" ");
				
				if (dropValues.length < 4) {
					Likeaboss.l.warning("[Likeaboss] Missing values for '" + bossName + ".Loot." + rollString + "." + dropEntry.getKey() + "' in bosses config file.");
					continue;
				}
				
				Material material = null;
				short metaData = 0;
				
				if (dropValues[0].contains(":")) {
					String[] tempData = dropValues[0].split(":");
					material = Material.getMaterial(Integer.valueOf(tempData[0]));
					metaData = Short.valueOf(tempData[1]);
				}
				else
					material = Material.getMaterial(Integer.valueOf(dropValues[0]));
				
				Drop drop = new Drop(material, metaData, Double.valueOf(dropValues[1]), Integer.valueOf(dropValues[2]), Integer.valueOf(dropValues[3]));
				
				roll.AddDrop(drop);
			}
			
			bossData.AddRoll(roll);
		}
	}
	
	public static void loadEquipment(BossData data, ConfigurationSection equipmentSection, String bossName){
		if (equipmentSection == null) {			
			return;
		}
		String helmet = equipmentSection.getString("Helmet");
		String chestplate = equipmentSection.getString("Chestplate");
		String leggings = equipmentSection.getString("Leggings");
		String boots = equipmentSection.getString("Boots");
		
		data.setEquipment(getArmorItemStack(helmet , 1), getArmorItemStack(chestplate , 2), getArmorItemStack(leggings , 3), getArmorItemStack(boots , 4), equipmentSection.getInt("Weapon"));		
	}
	
	private static ItemStack getArmorItemStack(String material, int type){
		if(material == null) return null;
		//1 = helmet, 2 = chestplate, 3 = leggings, 4 = boots		
		switch(type){
		case 1:
			if(material.equals("leather")) return new ItemStack(298);
			if(material.equals("chain")) return new ItemStack(302);
			if(material.equals("iron")) return new ItemStack(306);
			if(material.equals("gold")) return new ItemStack(314);
			if(material.equals("diamond")) return new ItemStack(310);
		case 2:
			if(material.equals("leather")) return new ItemStack(299);
			if(material.equals("chain")) return new ItemStack(303);
			if(material.equals("iron")) return new ItemStack(307);
			if(material.equals("gold")) return new ItemStack(315);
			if(material.equals("diamond")) return new ItemStack(311);
		case 3:
			if(material.equals("leather")) return new ItemStack(300);
			if(material.equals("chain")) return new ItemStack(304);
			if(material.equals("iron")) return new ItemStack(308);
			if(material.equals("gold")) return new ItemStack(316);
			if(material.equals("diamond")) return new ItemStack(312);
		case 4:
			if(material.equals("leather")) return new ItemStack(299);
			if(material.equals("chain")) return new ItemStack(303);
			if(material.equals("iron")) return new ItemStack(307);
			if(material.equals("gold")) return new ItemStack(315);
			if(material.equals("diamond")) return new ItemStack(311);
		default:
			return null;
		}
	}
	
	public static Map<String, BossData> getBossesData() {
		return bossesData;
	}	
}

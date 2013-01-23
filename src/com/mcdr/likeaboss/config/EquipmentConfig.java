package com.mcdr.likeaboss.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import com.mcdr.likeaboss.Likeaboss;
import com.mcdr.likeaboss.entity.EquipmentSet;


public class EquipmentConfig extends BaseConfig {
	public static Map<String, EquipmentSet> equipmentSets = new HashMap<String, EquipmentSet>();
	
	public static void Load(){
		File file = new File(Likeaboss.in.getDataFolder().getPath(), "equipment.yml");
		
		if (!file.exists())
			CopyResource(file, "com/mcdr/likeaboss/config/equipment.yml");
		
		loadEquipmentSets(LoadConfig(file));
	}
	
	public static void loadEquipmentSets(YamlConfiguration yamlConfig){
		Set<String> equipmentSetNames = yamlConfig.getKeys(false);
		equipmentSetNames.remove("ConfigVersion");
		for(String equipmentSetName: equipmentSetNames){
			ConfigurationSection configurationSection = yamlConfig.getConfigurationSection(equipmentSetName);
			
			if (configurationSection == null) {
				Likeaboss.l.warning("[Likeaboss] '" + equipmentSetName + "' in equipment config file is invalid.");
				continue;
			}
			
			int[][][] helmetData = processEquipmentSection(yamlConfig, configurationSection, "Helmet");
			int[][][] chestplateData = processEquipmentSection(yamlConfig, configurationSection, "Chestplate");
			int[][][] leggingsData = processEquipmentSection(yamlConfig, configurationSection, "Leggings");
			int[][][] bootsData = processEquipmentSection(yamlConfig, configurationSection, "Boots");
			int[][][] weaponData = processEquipmentSection(yamlConfig, configurationSection, "Weapon");
			equipmentSets.put(equipmentSetName, new EquipmentSet(helmetData, chestplateData, leggingsData, bootsData, weaponData, equipmentSetName));
		}		
	}
	
	public static int[][][] processEquipmentSection(YamlConfiguration yamlConfig, ConfigurationSection configurationSection, String equipmentType){
		ConfigurationSection equipSection = yamlConfig.getConfigurationSection(configurationSection.getCurrentPath()+"."+equipmentType);
		String equipSectionPath = equipSection.getCurrentPath()+".", eSPath;
		Set<String> equipment = equipSection.getKeys(false);
		int amount = equipment.size();
		
		// Initialise the necessary variables starting with the arrays
		int[] itemIds = new int[amount], probabilities = new int[amount], itemData = new int[amount], durability = new int[amount], dropChances = new int[amount];
		int[][] enchantmentIds = new int[amount][], enchantmentChances = new int[amount][], enchantmentLvls = new int[amount][];
		// The item variables
		int itemId, prob, data, dur, dropProb;
		// The enchantment variables
		String enchName;
		Enchantment ench;
		int chance, lvl, enchAmount;
		// The counters
		int i = -1, j;
		
		// Process all the different items
		for(String equip: equipment){
			j = -1;
			eSPath = equipSectionPath+equip;
			// Check if the config section actually exists
			if (yamlConfig.getConfigurationSection(eSPath) == null) {
				Likeaboss.l.warning("[Likeaboss] '" + equipSection.getCurrentPath() + equip + "' in the equipment config file is invalid.");
				continue;
			}
			// Check if the equipment is enabled
			if(!yamlConfig.getBoolean(eSPath+".Enabled"))
				continue;
			// Assign necessary variables for sanity checks & processing
			itemId = yamlConfig.getInt(eSPath+".ItemId");
			prob = yamlConfig.getInt(eSPath+".Probability");
			data = yamlConfig.getInt(eSPath+".ItemData");
			dur = yamlConfig.getInt(eSPath+".ItemDurability");
			dropProb = yamlConfig.getInt(eSPath+".DropProbability");
			
			// Actual sanity checks
			if(itemId<0){
				Likeaboss.l.warning("[Likeaboss] '" + eSPath + ".ItemId' in the equipment config file is invalid.");
				continue;
			}
			if(prob<=0){
				Likeaboss.l.warning("[Likeaboss] '" + eSPath + ".Probability' in the equipment config file is invalid.");
				continue;
			}
			if(data<0){
				Likeaboss.l.warning("[Likeaboss] '" + eSPath + ".ItemData' in the equipment config file is invalid.");
				continue;
			}
			if(dur<0){
				Likeaboss.l.warning("[Likeaboss] '" + eSPath + ".ItemDurability' in the equipment config file is invalid.");
				continue;
			}
			if(dropProb<0 || dropProb>100){
				Likeaboss.l.warning("[Likeaboss] '" + eSPath + ".DropProbability' in the equipment config file is invalid.");
				continue;
			}
			//End of sanity checks, the values are probably valid so increment the array counter
			i++;
			//And assign the values
			itemIds[i] = yamlConfig.getInt(eSPath+".ItemId");
			probabilities[i] = yamlConfig.getInt(eSPath+".Probability");
			itemData[i] = yamlConfig.getInt(eSPath+".ItemData");
			durability[i] = yamlConfig.getInt(eSPath+".ItemDurability");
			dropChances[i] = yamlConfig.getInt(eSPath+".DropProbability");
			
			// And now for the enchantments
			eSPath += ".Enchantments";
			if (yamlConfig.getConfigurationSection(eSPath) == null)
				continue;
			
			Set<String> enchantments = yamlConfig.getConfigurationSection(eSPath).getKeys(false);
			enchAmount = enchantments.size();
			enchantmentIds[i] = new int[enchAmount];
			enchantmentChances[i] = new int[enchAmount];
			enchantmentLvls[i] = new int[enchAmount];
			for(String enchantment: enchantments){
				// Check if the config section acutally exists
				if (yamlConfig.getConfigurationSection(eSPath+"."+enchantment) == null) {
					Likeaboss.l.warning("[Likeaboss] '" + enchantment + "' in the equipment config file is invalid.");
					continue;
				}
				// Check if the enchantment is enabled
				if(!yamlConfig.getBoolean(eSPath+"."+enchantment+".Enabled"))
					continue;
				
				// Assign necessary variables for sanity checks & processing
				enchName = yamlConfig.getString(eSPath+"."+enchantment+".Enchantment").trim().toUpperCase().replace(" ", "_");
				ench = Enchantment.getByName(enchName);
				chance = yamlConfig.getInt(eSPath+"."+enchantment+".Probability");
				lvl = yamlConfig.getInt(eSPath+"."+enchantment+".Level");
				
				// Actual sanity checks
				if(ench==null || enchName==null){
					Likeaboss.l.warning("[Likeaboss] '" + eSPath+"."+enchantment + ".Enchantment' in the equipment config file is invalid.");
					continue;
				}
				if(chance<=0){
					Likeaboss.l.warning("[Likeaboss] '" + eSPath+"."+enchantment + ".Probability' in the equipment config file is invalid.");
					continue;
				}
				if(lvl<=0){
					Likeaboss.l.warning("[Likeaboss] '" + eSPath+"."+enchantment + ".Level' in the equipment config file is invalid.");
					continue;
				}
				//End of sanity checks, the values are probably valid so increment the array counter
				j++;
				//And assign the values
				enchantmentIds[i][j] = ench.getId();
				enchantmentChances[i][j] = chance;
				enchantmentLvls[i][j] = lvl;
			}
			if(j+1<amount){
				enchantmentIds[i] = trimArrayAfterIndex(enchantmentIds[i], j);
				enchantmentChances[i] = trimArrayAfterIndex(enchantmentChances[i], j);
				enchantmentLvls[i] = trimArrayAfterIndex(enchantmentLvls[i], j);
			}
		}
		if(i+1<amount){
			itemIds = trimArrayAfterIndex(itemIds, i);
			probabilities = trimArrayAfterIndex(probabilities, i);
			itemData = trimArrayAfterIndex(itemData, i);
			durability = trimArrayAfterIndex(durability, i);
			dropChances = trimArrayAfterIndex(dropChances, i);
		}
			
		return EquipmentSet.makeDataArray(itemIds, probabilities, itemData, durability, dropChances, enchantmentIds, enchantmentChances, enchantmentLvls);
	}
	
	public static int[] trimArrayAfterIndex(int[] arr, int index) {
		ArrayList<Integer> removed = new ArrayList<Integer>();
		int i = -1;
		for (Integer integer : arr){
			i++;
			if (i<=index)
				removed.add(integer);
		}
		return ArrayUtils.toPrimitive(removed.toArray(new Integer[index+1]));
	}
}

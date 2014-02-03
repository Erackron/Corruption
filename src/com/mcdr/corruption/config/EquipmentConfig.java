package com.mcdr.corruption.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mcdr.corruption.entity.CorItem;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import com.mcdr.corruption.entity.EquipmentSet;
import com.mcdr.corruption.util.CorLogger;
import com.mcdr.corruption.util.legacy.EnchNames;


public class EquipmentConfig extends BaseConfig {
	public static Map<String, EquipmentSet> equipmentSets;
	
	public static void Load(){
		File file = new File(DATAFOLDER, "equipment.yml");
		
		if (!file.exists())
			copyResource(file, "com/mcdr/corruption/config/equipment.yml");
		
		loadEquipmentSets(loadConfig(file));
	}
	
	public static void loadEquipmentSets(YamlConfiguration yamlConfig){
		Set<String> equipmentSetNames = yamlConfig.getKeys(false);
		equipmentSetNames.remove("ConfigVersion");
		equipmentSets = new HashMap<String, EquipmentSet>();
		for(String equipmentSetName: equipmentSetNames){
			ConfigurationSection configurationSection = yamlConfig.getConfigurationSection(equipmentSetName);
			
			if (configurationSection == null) {
				CorLogger.w("'" + equipmentSetName + "' in equipment config file is invalid.");
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
				CorLogger.w("'" + eSPath + "' in the equipment config file is invalid.");
				continue;
			}
            CorItem item = ItemConfig.items.get(equip);
            if(item == null){
                CorLogger.w("'" + eSPath + "' in the equipment config file does not exist.");
                continue;
            }

			// Check if the equipment is enabled
			if(!yamlConfig.getBoolean(eSPath+".Enabled"))
				continue;
			// Assign necessary variables for sanity checks & processing
			itemId = item.getId();
			prob = yamlConfig.getInt(eSPath+".Probability");
			data = item.getData();
			dur = item.getDurability();
			dropProb = yamlConfig.getInt(eSPath+".DropProbability");
			
			// Actual sanity checks
			if(itemId<0){
				CorLogger.w("'" + eSPath + ".ItemId' in the equipment config file is invalid.");
				continue;
			}
			if(prob<=0){
				CorLogger.w("'" + eSPath + ".Probability' in the equipment config file is invalid.");
				continue;
			}
			if(data<0){
				CorLogger.w("'" + eSPath + ".ItemData' in the equipment config file is invalid.");
				continue;
			}
			if(dur<0){
				CorLogger.w("'" + eSPath + ".ItemDurability' in the equipment config file is invalid.");
				continue;
			}
			if(dropProb<0 || dropProb>100){
				CorLogger.w("'" + eSPath + ".DropProbability' in the equipment config file is invalid.");
				continue;
			}
			//End of sanity checks, the values are probably valid so increment the array counter
			i++;
			//And assign the values
			itemIds[i] = itemId;
			probabilities[i] = prob;
			itemData[i] = data;
            durability[i] = dur;
            dropChances[i] = dropProb;

            enchantmentIds[i] = item.getEnchantmentIds();
            enchantmentChances[i] = item.getEnchantmentChances();
            enchantmentLvls[i] = item.getEnchantmentLevels();
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

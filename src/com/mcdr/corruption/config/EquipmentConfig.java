package com.mcdr.corruption.config;

import com.mcdr.corruption.entity.CorItem;
import com.mcdr.corruption.entity.EquipmentSet;
import com.mcdr.corruption.util.CorLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class EquipmentConfig extends BaseConfig {
    public static Map<String, EquipmentSet> equipmentSets;

    public static void Load() {
        File file = new File(DATAFOLDER, "equipment.yml");

        if (!file.exists())
            copyResource(file, "com/mcdr/corruption/config/equipment.yml");

        loadEquipmentSets(loadConfig(file));
    }

    public static void loadEquipmentSets(YamlConfiguration yamlConfig) {
        Set<String> equipmentSetNames = yamlConfig.getKeys(false);
        equipmentSetNames.remove("ConfigVersion");
        equipmentSets = new HashMap<String, EquipmentSet>();
        for (String equipmentSetName : equipmentSetNames) {
            ConfigurationSection configurationSection = yamlConfig.getConfigurationSection(equipmentSetName);

            if (configurationSection == null) {
                CorLogger.w("'" + equipmentSetName + "' in equipment config file is invalid.");
                continue;
            }

            Map<CorItem, Integer[]> helmetData = processEquipmentSection(yamlConfig, configurationSection, "Helmet"),
                    chestplateData = processEquipmentSection(yamlConfig, configurationSection, "Chestplate"),
                    leggingsData = processEquipmentSection(yamlConfig, configurationSection, "Leggings"),
                    bootsData = processEquipmentSection(yamlConfig, configurationSection, "Boots"),
                    weaponData = processEquipmentSection(yamlConfig, configurationSection, "Weapon");
            equipmentSets.put(equipmentSetName, new EquipmentSet(helmetData, chestplateData, leggingsData, bootsData, weaponData, equipmentSetName));
        }
    }

    public static Map<CorItem, Integer[]> processEquipmentSection(YamlConfiguration yamlConfig, ConfigurationSection configurationSection, String equipmentType) {
        ConfigurationSection equipSection = yamlConfig.getConfigurationSection(configurationSection.getCurrentPath() + "." + equipmentType);
        String equipSectionPath = equipSection.getCurrentPath() + ".", eSPath;
        Set<String> equipment = equipSection.getKeys(false);

        Map<CorItem, Integer[]> items = new HashMap<CorItem, Integer[]>();
        // The item variables
        int prob, dropProb;

        // Process all the different items
        for (String equip : equipment) {
            eSPath = equipSectionPath + equip;
            // Check if the config section actually exists
            if (yamlConfig.getConfigurationSection(eSPath) == null) {
                CorLogger.w("'" + eSPath + "' in the equipment config file is invalid.");
                continue;
            }
            CorItem item = ItemConfig.items.get(equip);
            if (item == null) {
                CorLogger.w("'" + eSPath + "' in the equipment config file does not exist.");
                continue;
            }

            // Check if the equipment is enabled
            if (!yamlConfig.getBoolean(eSPath + ".Enabled"))
                continue;

            prob = yamlConfig.getInt(eSPath + ".Probability");
            dropProb = yamlConfig.getInt(eSPath + ".DropProbability");

            // Actual sanity checks
            if (prob <= 0) {
                CorLogger.w("'" + eSPath + ".Probability' in the equipment config file is invalid.");
                continue;
            }
            if (dropProb < 0 || dropProb > 100) {
                CorLogger.w("'" + eSPath + ".DropProbability' in the equipment config file is invalid.");
                continue;
            }

            items.put(item, new Integer[]{prob, dropProb});
        }
        return items;
    }
}

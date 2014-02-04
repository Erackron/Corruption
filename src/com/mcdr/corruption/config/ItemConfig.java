package com.mcdr.corruption.config;

import com.mcdr.corruption.entity.CorItem;
import com.mcdr.corruption.util.CorLogger;
import com.mcdr.corruption.util.legacy.EnchNames;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ItemConfig extends BaseConfig {

    public static HashMap<String, CorItem> items;

    public static void Load() {
        File file = new File(DATAFOLDER, "items.yml");

        if (!file.exists()) {
            copyResource(file, "com/mcdr/corruption/config/items.yml");
        }

        loadItems(loadConfig(file));
    }

    public static void loadItems(YamlConfiguration yamlConfiguration) {
        Set<String> itemNames = yamlConfiguration.getKeys(false);
        itemNames.remove("ConfigVersion");

        items = new HashMap<String, CorItem>();

        for (String itemName : itemNames) {
            ConfigurationSection configurationSection = yamlConfiguration.getConfigurationSection(itemName);

            if (configurationSection == null) {
                CorLogger.w("'" + itemName + "' in items config file is invalid.");
                continue;
            }

            CorItem item = processItemSection(configurationSection);
            if (item != null)
                items.put(itemName, item);
        }
    }

    public static CorItem processItemSection(ConfigurationSection configurationSection) {
        int id, data, durability;
        int[] enchantmentIds = new int[0], enchantmentChances = new int[0], enchantmentLevels = new int[0];

        String name;
        List<String> lore;

        // The enchantment variables
        String enchName;
        Enchantment ench;
        int chance, lvl, enchAmount;

        id = configurationSection.getInt("Id");
        data = configurationSection.getInt("Data");
        durability = configurationSection.getInt("Durability");
        name = configurationSection.getString("Name");
        lore = configurationSection.getStringList("Lore");

        if (id < 0) {
            CorLogger.w("'" + configurationSection.getCurrentPath() + ".Id' in the item config file is invalid");
            return null;
        }
        if (data < 0) {
            CorLogger.w("'" + configurationSection.getCurrentPath() + ".Data' in the item config file is invalid");
            return null;
        }
        if (durability < 0) {
            CorLogger.w("'" + configurationSection.getCurrentPath() + ".Durability' in the item config file is invalid");
            return null;
        }

        if (configurationSection.isConfigurationSection("Enchantments")) {
            ConfigurationSection enchantmentSection = configurationSection.getConfigurationSection("Enchantments");
            enchAmount = enchantmentSection.getKeys(false).size();
            enchantmentIds = new int[enchAmount];
            enchantmentChances = new int[enchAmount];
            enchantmentLevels = new int[enchAmount];
            int j = -1;
            for (String enchantment : enchantmentSection.getKeys(false)) {
                if (!enchantmentSection.getConfigurationSection(enchantment).getBoolean("Enabled"))
                    continue;

                enchName = enchantmentSection.getConfigurationSection(enchantment).getString("Enchantment").trim().toUpperCase().replace(" ", "_");
                ench = Enchantment.getByName(enchName);
                chance = enchantmentSection.getConfigurationSection(enchantment).getInt("Probability");
                lvl = enchantmentSection.getConfigurationSection(enchantment).getInt("Level");

                if (ench == null || enchName == null) {
                    CorLogger.w("'" + enchantmentSection.getCurrentPath() + "." + enchantment + ".Enchantment' in the equipment config file is invalid.");
                    continue;
                }
                if (chance <= 0) {
                    CorLogger.w("'" + enchantmentSection.getCurrentPath() + "." + enchantment + ".Probability' in the equipment config file is invalid.");
                    continue;
                }
                if (lvl <= 0) {
                    CorLogger.w("'" + enchantmentSection.getCurrentPath() + "." + enchantment + ".Level' in the equipment config file is invalid.");
                    continue;
                }

                j++;

                enchantmentIds[j] = EnchNames.getId(ench);
                enchantmentChances[j] = chance;
                enchantmentLevels[j] = lvl;
            }
        }
        ArrayList<String> parsedLore = new ArrayList<String>();
        for (String loreString : lore)
            parsedLore.add(ChatColor.translateAlternateColorCodes('&', loreString));

        return new CorItem(id, data, durability, enchantmentIds, enchantmentChances, enchantmentLevels, name, parsedLore);
    }
}

package com.mcdr.corruption.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.GlobalConfig;

public class CorConfigUpdater {

    private String latestVersion;

    public CorConfigUpdater(){
        latestVersion = Corruption.in.getDescription().getVersion();
    }

    public void updateFiles(){
        updateAbilityConfig();
        updateItemConfig();
        updateBossConfig();
        updateGlobalConfig();
        updateEquipmentConfig();
        updateMagicSpellsConfig();
        updateWorldConfig();
    }

    private void updateAbilityConfig(){
        YamlConfiguration ability = getYamlConfig(getFile("abilities.yml"));

        if(ability == null)
            return;

        String configVersion;
        if(ability.isSet("ConfigVersion"))
            configVersion = ability.getString("ConfigVersion");
        else{
            configVersion = "1.7.0";
        }
        if(!Utility.isOlderVersion(configVersion, latestVersion))
            return;

        CorLogger.i("Creating backup of abilities.yml!");
        File configFile = getFile("abilities.yml");
        File backupFile = new File(Corruption.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "abilities.yml");

        new File(backupFile.getParent()).mkdirs();

        try {
            Utility.fileToFile(configFile, backupFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CorLogger.i("Ability config backup created, updating abilities.yml");


        if(Utility.isOlderVersion(configVersion, "2.0")){
            ability = addConfigVersion("abilities.yml");
            for(String node : ability.getKeys(false)){
                ConfigurationSection section = ability.getConfigurationSection(node);

                if(section == null)
                    continue;

                if(!ability.isSet(node + ".Probability")){
                    CorLogger.w("Missing values for ability '" + node + ".Probability' in abilities.yml");
                    continue;
                }
                double d = section.getDouble(node + ".Probability");
                ability.set(node + ".Probability", null);
                ability.set(node + ".ActivationChance", d);

                ability.set(node + ".AssignationChance", 100.0D);
                ability.set(node + ".MinimumRange", 0);
                ability.set(node + ".MaximumRange", 16);

                if(ability.getString(node + ".Type").equals("Bomb")){
                    if(!ability.isSet(node + ".Radius")){
                        CorLogger.w("Missing values for ability '" + node + "' in abilities.yml");
                        continue;
                    }
                    int i = ability.getInt(node + ".Radius");
                    ability.set(node + ".Radius", null);
                    ability.set(node + ".ExplosionRadius", i);
                }

                if(ability.getString(node + ".Type").equals("LightningAura")){
                    if(!ability.isSet(node + ".Radius")){
                        CorLogger.w("Missing values for ability '" + node + "' in abilities.yml");
                        continue;
                    }
                    int i = ability.getInt(node + ".Radius");
                    ability.set(node + ".Radius", null);
                    ability.set(node + ".MaximumRange", i);
                }
            }
        }

        if(Utility.isOlderVersion(configVersion, "2.1")){
            CorLogger.i("Updating abilities.yml");
            ability.getKeys(false).remove("ConfigVersion");
            List<String> conditions = new ArrayList<String>();
            conditions.add("OnAttack");
            conditions.add("OnDefense");

            for(String node : ability.getKeys(false)){

                if(!ability.isSet(node+".Type")){
                    CorLogger.i("Missing type in abilities.yml");
                    continue;
                }

                if(ability.getString(node + ".Type").equalsIgnoreCase("FirePunch")){
                    node += ".ActivationConditions";
                    ability.set(node, conditions.subList(0, 1));
                } else if(ability.getString(node + ".Type").equalsIgnoreCase("ArmorPierce")){
                    node += ".ActivationConditions";
                    ability.set(node, conditions.subList(0, 1));
                } else if(ability.getString(node + ".Type").equalsIgnoreCase("Knockback")){
                    node += ".ActivationConditions";
                    ability.set(node, conditions.subList(0, 1));
                } else if(ability.getString(node + ".Type").equalsIgnoreCase("Bomb")){
                    node += ".ActivationConditions";
                    ability.set(node, conditions.subList(0, 2));
                } else if(ability.getString(node + ".Type").equalsIgnoreCase("LightningAura")){
                    node += ".ActivationConditions";
                    ability.set(node, conditions.subList(0, 1));
                } else if(ability.getString(node + ".Type").equalsIgnoreCase("Potion")){
                    node += ".ActivationConditions";
                    ability.set(node, conditions.subList(0, 2));
                } else if(ability.getString(node + ".Type").equalsIgnoreCase("Teleport")){
                    node += ".ActivationConditions";
                    ability.set(node, conditions.subList(0, 2));
                }
            }
        }

        ability.set("ConfigVersion", latestVersion);
        save(ability, "abilities.yml");
        CorLogger.i("Ability config updated");

    }

    private void updateGlobalConfig(){
        YamlConfiguration global = getYamlConfig(getFile("config.yml"));

        if(global == null)
            return;

        String configVersion;
        if(global.isSet("ConfigVersion"))
            configVersion = global.getString("ConfigVersion");
        else{
            configVersion = "1.7.0";
        }
        if(!Utility.isOlderVersion(configVersion, latestVersion))
            return;

        CorLogger.i("Creating backup of config.yml");
        File configFile = getFile("config.yml");
        File backupFile = new File(Corruption.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "config.yml");

        new File(backupFile.getParent()).mkdirs();

        try {
            Utility.fileToFile(configFile, backupFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CorLogger.i("Global config backup created, updating config.yml");

        if(Utility.isOlderVersion(configVersion, "2.0")){
            global = addConfigVersion("config.yml");
            global.set("Boss.Immunity", null);
        }

        if(Utility.isOlderVersion(configVersion, "2.1")){
            if(global.isSet("Task.CheckEntityHealth"))
                global.set("Task.CheckEntityHealth", null);

            if(!global.isSet("Message.ViewerDamageAbsorbed"))
                global.set("Message.ViewerDamageAbsorbed", GlobalConfig.MessageParam.VIEWERDAMAGEABSORBED.getMessage());

            if(!global.isSet("Task.LoadPlayerData"))
                global.set("Task.LoadPlayerData", 5.0);
        }

        if(Utility.isOlderVersion(configVersion, "2.1.2")){
            if(!global.isSet("CheckUpdateOnStartup"))
                global.set("CheckUpdateOnStartup", true);

            if(!global.isSet("ReloadAfterUpdating"))
                global.set("ReloadAfterUpdating", true);
        }

        if(Utility.isOlderVersion(configVersion, "2.2")){
            if(global.isSet("Message.ViewerMessage"))
                global.set("Message.ViewerMessage", global.getString("Message.ViewerMessage").replace("{HEALTH}", "&7{HEALTH}"));
            else
                global.set("Message.ViewerMessage", GlobalConfig.MessageParam.VIEWERMESSAGE.getMessage());

            if(!global.isSet("Message.CustomBossName"))
                global.set("Message.CustomBossName", GlobalConfig.MessageParam.CUSTOMBOSSNAME.getMessage());

            if(!global.isSet("Boss.EnableBiomes"))
                global.set("Boss.EnableBiomes", false);
        }

        if(Utility.isOlderVersion(configVersion, "2.2.1")){
            String cbn = global.getString("Message.CustomBossName", "nothing");
            if(cbn.equalsIgnoreCase("hide"))
                global.set("Message.CustomBossName", "false");
            else if(cbn.equalsIgnoreCase("nothing"))
                global.set("Message.CustomBossName", GlobalConfig.MessageParam.CUSTOMBOSSNAME.getMessage());
        }

        global.set("ConfigVersion", latestVersion);
        save(global, "config.yml");
        CorLogger.i("Global config updated");
    }

    private void updateBossConfig(){
        YamlConfiguration bosses = getYamlConfig(getFile("bosses.yml"));

        if(bosses == null)
            return;

        String configVersion;
        if(bosses.isSet("ConfigVersion"))
            configVersion = bosses.getString("ConfigVersion");
        else{
            configVersion = "1.7.0";
        }
        if(!Utility.isOlderVersion(configVersion, latestVersion))
            return;

        CorLogger.i("Creating backup of bosses.yml");

        File configFile = getFile("bosses.yml");
        File backupFile = new File(Corruption.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "bosses.yml");

        new File(backupFile.getParent()).mkdirs();

        try {
            Utility.fileToFile(configFile, backupFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CorLogger.i("Bosses config backup created, updating bosses.yml");

        if(Utility.isOlderVersion(configVersion, "2.0")){
            bosses = addConfigVersion("bosses.yml");
            YamlConfiguration global = getYamlConfig(getFile("config.yml"));
            ConfigurationSection immunity = global.getConfigurationSection("Boss.Immunity");

            for(String node : bosses.getKeys(false)){
                if(node.equalsIgnoreCase("ConfigVersion"))
                    continue;

                String spawnString = bosses.getString(node+".Spawn");
                if (spawnString == null) {
                    CorLogger.w("'" + node + "' in bosses config file is missing.");
                    return;
                }

                String[] spawnValues = spawnString.split(" ");

                if (spawnValues.length < 2) {
                    CorLogger.w("Missing values for '" + node + ".Spawn' in bosses config file");
                    return;
                } else if (spawnValues.length < 3){
                    String[] temp = new String[spawnValues.length + 1];
                    System.arraycopy(spawnValues, 0, temp, 0, spawnValues.length);
                    temp[spawnValues.length] = "256";
                    spawnValues = temp;
                }

                bosses.set(node + ".Spawn", null);

                bosses.createSection(node + ".Spawn");

                bosses.set(node + ".Spawn.Probability", Double.parseDouble(spawnValues[0]));
                bosses.set(node + ".Spawn.SpawnerProbability", Double.parseDouble(spawnValues[1]));
                bosses.set(node + ".Spawn.MaxSpawnHeight", Double.parseDouble(spawnValues[2]));

                if (!bosses.isSet(node + ".Stats")) {
                    CorLogger.w("'" + node + ".Stats' in bosses config file is missing.");
                    return;
                }

                String statsString = bosses.getString(node + ".Stats");
                String[] statsValues = statsString.split(" ");

                if (statsValues.length < 3) {
                    CorLogger.w("Missing values for '" + node + ".Stats' in bosses config file");
                    return;
                }

                bosses.set(node + ".Stats", null);

                bosses.createSection(node + ".Stats");

                bosses.set(node + ".Stats.Health", Double.parseDouble(statsValues[0]));
                bosses.set(node + ".Stats.Damage", Double.parseDouble(statsValues[1]));
                bosses.set(node + ".Stats.Experience", Double.parseDouble(statsValues[2]));

                for(String s: immunity.getKeys(true)){
                    if(immunity.getBoolean(s))
                        bosses.set(node + ".Immunity." +s, immunity.getBoolean(s));
                }

                bosses.set(node + ".mcMMOXPBonus", 0);

                String entityType = bosses.getString(node+".EntityType");
                if(entityType.equalsIgnoreCase("pigzombie")){
                    if(!bosses.isSet(node + ".Aggressive"))
                        bosses.set(node + ".Aggressive", true);
                    if(!bosses.isSet(node + ".Baby"))
                        bosses.set(node + ".Baby", false);
                } else if(entityType.equalsIgnoreCase("zombie")){
                    if(!bosses.isSet(node + ".Baby"))
                        bosses.set(node + ".Baby", false);
                    if(!bosses.isSet(node + ".Villager"))
                        bosses.set(node + ".Villager", false);
                } else if(entityType.equalsIgnoreCase("skeleton")){
                    if(!bosses.isSet(node + ".WitherSkeleton"))
                        bosses.set(node + ".WitherSkeleton", false);
                } else if(entityType.equalsIgnoreCase("witherboss")){
                    if(!bosses.isSet(node + ".HealthRegenPerSecond"))
                        bosses.set(node + ".HealthRegenPerSecond", 2);
                } else if(entityType.equalsIgnoreCase("slime") || entityType.equalsIgnoreCase("lavaslime")){
                    if(!bosses.isSet(node + ".MinimumSize"))
                        bosses.set(node + ".MinimumSize", 2);
                    if(!bosses.isSet(node + ".MaximumSize"))
                        bosses.set(node + ".MaximumSize", 4);
                }
            }
        }

        if(Utility.isOlderVersion(configVersion, "2.1")){
            for(String node : bosses.getKeys(false)){
                if(node.equalsIgnoreCase("ConfigVersion"))
                    continue;
                String entityType = bosses.getString(node+".EntityType");
                if(entityType.equalsIgnoreCase("ghast")){
                    if(!bosses.isSet(node + ".ReturnToSenderImmune"))
                        bosses.set(node + ".ReturnToSenderImmune", true);
                }
            }
        }

        if(Utility.isOlderVersion(configVersion, "2.1.1")){
            for(String node : bosses.getKeys(false)){
                if(node.equalsIgnoreCase("ConfigVersion"))
                    continue;
                if(!bosses.isSet(node+".HeroesKillingExperience"))
                    bosses.set(node+".HeroesKillingExperience", 0.0D);
            }
        }

        if(Utility.isOlderVersion(configVersion, "2.2")){
            for(String node : bosses.getKeys(false)){
                if(node.equalsIgnoreCase("ConfigVersion"))
                    continue;
                if(!bosses.isSet(node+".Spawn.MinSpawnHeight"))
                    bosses.set(node+".Spawn.MinSpawnHeight", 0.0D);
            }
        }

        if(Utility.isOlderVersion(configVersion, "2.4")){
            try {
                PrintWriter writer = new PrintWriter(getFile("items.yml"));
                writer.print("");
                writer.close();
            } catch (FileNotFoundException ignore) {}

            YamlConfiguration itemsConfiguration = getYamlConfig(getFile("items.yml"));
            for(String node : bosses.getKeys(false)){
                if(node.equalsIgnoreCase("ConfigVersion"))
                    continue;
                if(!bosses.isSet(node+".Loot")){
                    CorLogger.w("'"+node+".Loot' in bosses config file is missing");
                    continue;
                }

                ConfigurationSection lootSection = bosses.getConfigurationSection(node+".Loot");
                for(String rollNode : lootSection.getKeys(false)){
                    ConfigurationSection rollSection = bosses.getConfigurationSection(node+".Loot."+rollNode);
                    for(String itemNode : rollSection.getKeys(false)){
                        String item = rollSection.getString(itemNode);
                        String name = node+"_"+rollNode+"_"+itemNode;
                        String[] itemValues = item.split(" ");

                        if(itemValues.length<4){
                            CorLogger.w("Missing values for '" + node + ".Loot." + rollNode + "." + itemNode + "' in bosses config file.");
                            continue;
                        }

                        putItemInItemsConfig(itemsConfiguration, name, itemValues[0]);

                        rollSection.set(itemNode, null);
                        ConfigurationSection itemSection = rollSection.createSection(name);
                        itemSection.set("Probability", Double.parseDouble(itemValues[1]));
                        itemSection.set("MinQuantity", Integer.parseInt(itemValues[2]));
                        itemSection.set("MaxQuantity", Integer.parseInt(itemValues[3]));
                    }
                }
            }
            save(itemsConfiguration, "items.yml");
        }

        bosses.set("ConfigVersion", latestVersion);
        save(bosses, "bosses.yml");
        CorLogger.i("Boss config updated");
    }

    private void updateEquipmentConfig(){
        YamlConfiguration equipment = getYamlConfig(getFile("equipment.yml"));

        if(equipment == null)
            return;

        String configVersion;
        if(equipment.isSet("ConfigVersion"))
            configVersion = equipment.getString("ConfigVersion");
        else{
            configVersion = "1.7.0";
        }
        if(!Utility.isOlderVersion(configVersion, latestVersion))
            return;

        CorLogger.i("Creating backup of equipment.yml");
        File configFile = getFile("equipment.yml");
        File backupFile = new File(Corruption.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "equipment.yml");

        new File(backupFile.getParent()).mkdirs();

        try {
            Utility.fileToFile(configFile, backupFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CorLogger.i("Equipment config backup created, updating equipment.yml");

        if(Utility.isOlderVersion(configVersion, "2.0")){
            equipment = addConfigVersion("equipment.yml");
        }

        equipment.set("ConfigVersion", latestVersion);
        save(equipment, "equipment.yml");
        CorLogger.i("Equipment config updated");
    }

    private void updateMagicSpellsConfig(){
        YamlConfiguration magicSpells = getYamlConfig(getFile("magicspells.yml"));

        if(magicSpells == null)
            return;

        String configVersion;
        if(magicSpells.isSet("ConfigVersion"))
            configVersion = magicSpells.getString("ConfigVersion");
        else{
            configVersion = "1.7.0";
        }
        if(!Utility.isOlderVersion(configVersion, latestVersion))
            return;

        CorLogger.i("Creating backup of magicspells.yml");
        File configFile = getFile("magicspells.yml");
        File backupFile = new File(Corruption.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "magicspells.yml");

        new File(backupFile.getParent()).mkdirs();

        try {
            Utility.fileToFile(configFile, backupFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CorLogger.i("MagicSpells config backup created, updating magicspells.yml");

        if(Utility.isOlderVersion(configVersion, "2.0")){
            magicSpells = addConfigVersion("magicspells.yml");
        }

        magicSpells.set("ConfigVersion", latestVersion);
        save(magicSpells, "magicspells.yml");
        CorLogger.i("MagicSpells config updated");
    }

    private void updateWorldConfig(){
        for(World world : Bukkit.getServer().getWorlds()){
            YamlConfiguration worldConfig = getYamlConfig(getFile("Worlds" + File.separator + world.getName() + ".yml"));

            if(worldConfig == null)
                continue;

            String configVersion;
            if(worldConfig.isSet("ConfigVersion"))
                configVersion = worldConfig.getString("ConfigVersion");
            else{
                configVersion = "1.7.0";
            }
            if(!Utility.isOlderVersion(configVersion, latestVersion))
                continue;

            CorLogger.i("Creating backup of " + world.getName() + ".yml");
            File configFile = getFile("Worlds" + File.separator + world.getName() + ".yml");
            File backupFile = new File(Corruption.in.getDataFolder().getPath(), "old_files" + File.separator +  "v" + configVersion + File.separator + "Worlds" + File.separator + world.getName() + ".yml");

            new File(backupFile.getParent()).mkdirs();

            try {
                Utility.fileToFile(configFile, backupFile);
            } catch (Exception e) {
                e.printStackTrace();
            }

            CorLogger.i("World " + world.getName() + " config backup created, updating " + world.getName() + ".yml");

            if(Utility.isOlderVersion(configVersion, "2.0")){
                worldConfig = addConfigVersion("Worlds" + File.separator + world.getName() + ".yml");
            }

            if(Utility.isOlderVersion(configVersion, "2.4")){
                YamlConfiguration itemsConfiguration = getYamlConfig(getFile("items.yml"));
                ConfigurationSection lootSection = worldConfig.getConfigurationSection("Loot");
                for(String rollNode : lootSection.getKeys(false)){
                    ConfigurationSection rollSection = worldConfig.getConfigurationSection("Loot."+rollNode);
                    for(String itemNode : rollSection.getKeys(false)){
                        String item = rollSection.getString(itemNode);
                        String name = world.getName()+"_"+rollNode+"_"+itemNode;
                        String[] itemValues = item.split(" ");

                        if(itemValues.length<4){
                            CorLogger.w("Missing values for 'Loot." + rollNode + "." + itemNode + "' in "+ world.getName() +" config file.");
                            continue;
                        }

                        putItemInItemsConfig(itemsConfiguration, name, itemValues[0]);

                        rollSection.set(itemNode, null);
                        ConfigurationSection itemSection = rollSection.createSection(name);
                        itemSection.set("Probability", Double.parseDouble(itemValues[1]));
                        itemSection.set("MinQuantity", Integer.parseInt(itemValues[2]));
                        itemSection.set("MaxQuantity", Integer.parseInt(itemValues[3]));
                    }
                }
                save(itemsConfiguration, "items.yml");
            }

            worldConfig.set("ConfigVersion", latestVersion);
            save(worldConfig, "Worlds"+ File.separator + world.getName()+".yml");
            CorLogger.i(""+world.getName()+" config updated");
        }
    }

    private void updateItemConfig(){
        YamlConfiguration items = getYamlConfig(getFile("items.yml"));

        if(items==null)
            return;

        String configVersion;
        if(items.isSet("ConfigVersion")){
            configVersion = items.getString("ConfigVersion");
        } else {
            configVersion = "1.7.0";
        }
        if(!Utility.isOlderVersion(configVersion, latestVersion)){
            return;
        }

        CorLogger.i("Creating backup uf items.yml");
        File configFile = getFile("items.yml");
        File backupFile = new File(Corruption.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "items.yml");

        new File(backupFile.getParent()).mkdirs();

        try {
            Utility.fileToFile(configFile, backupFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CorLogger.i("Items config backup created, updating items.yml");
    }

    private YamlConfiguration getYamlConfig(File file){
        YamlConfiguration yamlConfig = new YamlConfiguration();

        try {
            yamlConfig.load(file);
        } catch (Exception e) {
            return null;
        }

        return yamlConfig;
    }

    private File getFile(String name){
        return new File(Corruption.in.getDataFolder().getPath() + File.separatorChar + name);
    }

    /**
     * Add the latest config version to the config file and return the updated YamlConfiguration object
     * @param fileName the name of the file to add the latest config version to
     * @return the updated YamlConfiguration object
     */
    private YamlConfiguration addConfigVersion(String fileName){
        try {
            PrintWriter stream = new PrintWriter(new BufferedWriter(new FileWriter(getFile(fileName), true)));
            stream.println();
            stream.println();
            stream.print("ConfigVersion: '" + latestVersion + "'");

            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return getYamlConfig(getFile(fileName));
        }
        return getYamlConfig(getFile(fileName));
    }

    private void putItemInItemsConfig(YamlConfiguration itemsConfiguration, String name, String item){
        ConfigurationSection section = itemsConfiguration.createSection(name);
        String[] itemValues = {item, "0"};
        if(item.contains(":")){
            itemValues = item.split(":");
        }
        section.set("Id", Integer.parseInt(itemValues[0]));
        section.set("Data", Integer.parseInt(itemValues[1]));
        section.set("Durability", 0);
    }

    private boolean save(YamlConfiguration config, String fileName){
        try {
            config.save(getFile(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
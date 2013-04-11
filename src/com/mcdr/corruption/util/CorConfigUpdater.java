package com.mcdr.corruption.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
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
		
		Corruption.l.info("["+Corruption.pluginName+"] Creating backup of abilities.yml!");
		File configFile = getFile("abilities.yml");
		File backupFile = new File(Corruption.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "abilities.yml");
		
		((File) new File(backupFile.getParent())).mkdirs();
		
		try {
			Utility.fileToFile(configFile, backupFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Corruption.l.info("["+Corruption.pluginName+"] Ability config backup created, updating abilities.yml");
		
		
		if(Utility.isOlderVersion(configVersion, "2.0")){
			ability = addConfigVersion("abilities.yml");
			for(String node : ability.getKeys(false)){
				ConfigurationSection section = ability.getConfigurationSection(node);
				
				if(section == null)
					continue;
				
				if(!ability.isSet(node + ".Probability")){
					Corruption.l.warning("["+Corruption.pluginName+"] Missing values for ability '" + node + ".Probability' in abilities.yml");
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
						Corruption.l.warning("["+Corruption.pluginName+"] Missing values for ability '" + node + "' in abilities.yml");
						continue;
					}
					int i = ability.getInt(node + ".Radius");
					ability.set(node + ".Radius", null);
					ability.set(node + ".ExplosionRadius", i);
				}
				
				if(ability.getString(node + ".Type").equals("LightningAura")){
					if(!ability.isSet(node + ".Radius")){
						Corruption.l.warning("["+Corruption.pluginName+"] Missing values for ability '" + node + "' in abilities.yml");
						continue;
					}
					int i = ability.getInt(node + ".Radius");
					ability.set(node + ".Radius", null);
					ability.set(node + ".MaximumRange", i);
				}
			}			
		}
		
		if(Utility.isOlderVersion(configVersion, "2.1")){
			Corruption.l.info("["+Corruption.pluginName+"] Updating abilities.yml");
			ability.getKeys(false).remove("ConfigVersion");
			List<String> conditions = new ArrayList<String>();
			conditions.add("OnAttack");
			conditions.add("OnDefense");
			
			for(String node : ability.getKeys(false)){
				
				if(!ability.isSet(node+".Type")){
					Corruption.l.info("["+Corruption.pluginName+"] Missing type in abilities.yml");
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
		Corruption.l.info("["+Corruption.pluginName+"] Ability config updated");
		
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
		
		Corruption.l.info("["+Corruption.pluginName+"] Creating backup of config.yml");
		File configFile = getFile("config.yml");
		File backupFile = new File(Corruption.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "config.yml");
		
		((File) new File(backupFile.getParent())).mkdirs();
		
		try {
			Utility.fileToFile(configFile, backupFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Corruption.l.info("["+Corruption.pluginName+"] Global config backup created, updating config.yml");
		
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
		
		global.set("ConfigVersion", latestVersion);
		save(global, "config.yml");
		Corruption.l.info("["+Corruption.pluginName+"] Global config updated");
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
		
		Corruption.l.info("["+Corruption.pluginName+"] Creating backup of bosses.yml");
		
		File configFile = getFile("bosses.yml");
		File backupFile = new File(Corruption.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "bosses.yml");
		
		((File) new File(backupFile.getParent())).mkdirs();
		
		try {
			Utility.fileToFile(configFile, backupFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Corruption.l.info("["+Corruption.pluginName+"] Bosses config backup created, updating bosses.yml");
				
		if(Utility.isOlderVersion(configVersion, "2.0")){
			bosses = addConfigVersion("bosses.yml");
			YamlConfiguration global = getYamlConfig(getFile("config.yml"));
			ConfigurationSection immunity = global.getConfigurationSection("Boss.Immunity");
			
			for(String node : bosses.getKeys(false)){
				if(node.equalsIgnoreCase("ConfigVersion"))
					continue;
				
				String spawnString = bosses.getString(node+".Spawn");
				if (spawnString == null) {
					Corruption.l.warning("["+Corruption.pluginName+"] '" + node + "' in bosses config file is missing.");
					return;
				}
				
				String[] spawnValues = spawnString.split(" ");
				
				if (spawnValues.length < 2) {
					Corruption.l.warning("["+Corruption.pluginName+"] Missing values for '" + node + ".Spawn' in bosses config file");
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
					Corruption.l.warning("["+Corruption.pluginName+"] '" + node + ".Stats' in bosses config file is missing.");
					return;
				}
				
				String statsString = bosses.getString(node + ".Stats");				
				String[] statsValues = statsString.split(" ");
				
				if (statsValues.length < 3) {
					Corruption.l.warning("["+Corruption.pluginName+"] Missing values for '" + node + ".Stats' in bosses config file");
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
		
		bosses.set("ConfigVersion", latestVersion);
		save(bosses, "bosses.yml");
		Corruption.l.info("["+Corruption.pluginName+"] Boss config updated");
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
		
		Corruption.l.info("["+Corruption.pluginName+"] Creating backup of equipment.yml");
		File configFile = getFile("equipment.yml");
		File backupFile = new File(Corruption.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "equipment.yml");
		
		((File) new File(backupFile.getParent())).mkdirs();
		
		try {
			Utility.fileToFile(configFile, backupFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Corruption.l.info("["+Corruption.pluginName+"] Equipment config backup created, updating equipment.yml");
		
		if(Utility.isOlderVersion(configVersion, "2.0")){
			equipment = addConfigVersion("equipment.yml");
		}
		
		equipment.set("ConfigVersion", latestVersion);
		save(equipment, "equipment.yml");
		Corruption.l.info("["+Corruption.pluginName+"] Equipment config updated");
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
		
		Corruption.l.info("["+Corruption.pluginName+"] Creating backup of magicspells.yml");
		File configFile = getFile("magicspells.yml");
		File backupFile = new File(Corruption.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "magicspells.yml");
		
		((File) new File(backupFile.getParent())).mkdirs();
		
		try {
			Utility.fileToFile(configFile, backupFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Corruption.l.info("["+Corruption.pluginName+"] MagicSpells config backup created, updating magicspells.yml");
		
		if(Utility.isOlderVersion(configVersion, "2.0")){
			magicSpells = addConfigVersion("magicspells.yml");
		}
		
		magicSpells.set("ConfigVersion", latestVersion);
		save(magicSpells, "magicspells.yml");
		Corruption.l.info("["+Corruption.pluginName+"] MagicSpells config updated");
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
			
			Corruption.l.info("["+Corruption.pluginName+"] Creating backup of " + world.getName() + ".yml");
			File configFile = getFile("Worlds" + File.separator + world.getName() + ".yml");
			File backupFile = new File(Corruption.in.getDataFolder().getPath(), "old_files" + File.separator +  "v" + configVersion + File.separator + "Worlds" + File.separator + world.getName() + ".yml");
			
			((File) new File(backupFile.getParent())).mkdirs();
			
			try {
				Utility.fileToFile(configFile, backupFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Corruption.l.info("["+Corruption.pluginName+"] World " + world.getName() + " config backup created, updating " + world.getName() + ".yml");
			
			if(Utility.isOlderVersion(configVersion, "2.0")){
				worldConfig = addConfigVersion("Worlds" + File.separator + world.getName() + ".yml");
			}
			
			worldConfig.set("ConfigVersion", latestVersion);
			save(worldConfig, "Worlds"+ File.separator + world.getName()+".yml");
			Corruption.l.info("["+Corruption.pluginName+"] "+world.getName()+" config updated");
		}
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
		return ((File) new File(Corruption.in.getDataFolder().getPath() + File.separatorChar + name));
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
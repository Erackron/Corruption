package com.mcdr.likeaboss.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mcdr.likeaboss.Likeaboss;

public class LabConfigUpdater {
	
	private String latestVersion;
	
	public LabConfigUpdater(){
		latestVersion = Likeaboss.in.getDescription().getVersion();
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
		
		Likeaboss.l.info("[Likeaboss] Creating backup of abilities.yml!");
		File configFile = getFile("abilities.yml");
		File backupFile = new File(Likeaboss.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "abilities.yml");
		
		((File) new File(backupFile.getParent())).mkdirs();
		
		try {
			Utility.fileToFile(configFile, backupFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Likeaboss.l.info("[Likeaboss] Ability config backup created");
		
		if(Utility.isOlderVersion(configVersion, "2.0")){
			Likeaboss.l.info("[Likeaboss] Updating abilities.yml");
			try {
				PrintWriter stream = new PrintWriter(new BufferedWriter(new FileWriter(getFile("abilities.yml"), true)));
				stream.println();
				stream.println();
				stream.println("version: " + latestVersion);
				
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Likeaboss.l.info("[Likeaboss] Ability config updated");
		}
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
		
		Likeaboss.l.info("[Likeaboss] Creating backup of config.yml");
		File configFile = getFile("config.yml");
		File backupFile = new File(Likeaboss.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "config.yml");
		
		((File) new File(backupFile.getParent())).mkdirs();
		
		try {
			Utility.fileToFile(configFile, backupFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Likeaboss.l.info("[Likeaboss] Global config backup created");
		
		if(Utility.isOlderVersion(configVersion, "2.0")){
			Likeaboss.l.info("[Likeaboss] Updating config.yml");
			global.set("Boss.Immunity", null);
			try {
				global.save(getFile("config.yml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				PrintWriter stream = new PrintWriter(new BufferedWriter(new FileWriter(getFile("config.yml"), true)));
				stream.println();
				stream.println();
				stream.println("version: " + latestVersion);
				
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Likeaboss.l.info("[Likeaboss] Global config updated");
		}
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
		
		Likeaboss.l.info("[Likeaboss] Creating backup of bosses.yml");
		
		File configFile = getFile("bosses.yml");
		File backupFile = new File(Likeaboss.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "bosses.yml");
		
		((File) new File(backupFile.getParent())).mkdirs();
		
		try {
			Utility.fileToFile(configFile, backupFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Likeaboss.l.info("[Likeaboss] Bosses config backup created");
				
		if(Utility.isOlderVersion(configVersion, "2.0")){
			Likeaboss.l.info("[Likeaboss] Updating bosses.yml");
			YamlConfiguration global = getYamlConfig(getFile("config.yml"));
			ConfigurationSection immunity = global.getConfigurationSection("Boss.Immunity");
			
			for(String node : bosses.getKeys(false)){
				String spawnString = bosses.getString(node+".Spawn");
				if (spawnString == null) {
					Likeaboss.l.warning("[Likeaboss] '" + node + "' in bosses config file is missing.");
					return;
				}
				
				String[] spawnValues = spawnString.split(" ");
				
				if (spawnValues.length < 2) {
					Likeaboss.l.warning("[Likeaboss] Missing values for '" + node + ".Spawn' in bosses config file");
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
				
				String statsString = bosses.getString(node + ".Stats");
				
				if (statsString == null) {
					Likeaboss.l.warning("[Likeaboss] '" + node + ".Stats' in bosses config file is missing.");
					return;
				}
				
				String[] statsValues = statsString.split(" ");
				
				if (statsValues.length < 3) {
					Likeaboss.l.warning("[Likeaboss] Missing values for '" + node + ".Stats' in bosses config file");
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
			}			
			
			try {
				bosses.save(getFile("bosses.yml"));				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				PrintWriter stream = new PrintWriter(new BufferedWriter(new FileWriter(getFile("bosses.yml"), true)));
				stream.println();
				stream.println();
				stream.println("version: " + latestVersion);
				
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
			Likeaboss.l.info("[Likeaboss] Bosses config updated");
		}
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
		
		Likeaboss.l.info("[Likeaboss] Creating backup of equipment.yml");
		File configFile = getFile("equipment.yml");
		File backupFile = new File(Likeaboss.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "equipment.yml");
		
		((File) new File(backupFile.getParent())).mkdirs();
		
		try {
			Utility.fileToFile(configFile, backupFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Likeaboss.l.info("[Likeaboss] Equipment config backup created");
		
		if(Utility.isOlderVersion(configVersion, "2.0")){
			Likeaboss.l.info("[Likeaboss] Updating equipment.yml");
			try {
				PrintWriter stream = new PrintWriter(new BufferedWriter(new FileWriter(getFile("equipment.yml"), true)));
				stream.println();
				stream.println();
				stream.println("version: " + latestVersion);
				
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Likeaboss.l.info("[Likeaboss] Equipment config updated");
		}
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
		
		Likeaboss.l.info("[Likeaboss] Creating backup of magicspells.yml");
		File configFile = getFile("magicspells.yml");
		File backupFile = new File(Likeaboss.in.getDataFolder().getPath(), "old_files" + File.separator + "v" + configVersion + File.separator + "magicspells.yml");
		
		((File) new File(backupFile.getParent())).mkdirs();
		
		try {
			Utility.fileToFile(configFile, backupFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Likeaboss.l.info("[Likeaboss] MagicSpells config backup created");
		
		if(Utility.isOlderVersion(configVersion, "2.0")){
			Likeaboss.l.info("[Likeaboss] Updating magicspells.yml");
			try {
				PrintWriter stream = new PrintWriter(new BufferedWriter(new FileWriter(getFile("magicspells.yml"), true)));
				stream.println();
				stream.println();
				stream.println("version: " + latestVersion);
				
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Likeaboss.l.info("[Likeaboss] MagicSpells config updated");
		}
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
			
			Likeaboss.l.info("[Likeaboss] Creating backup of " + world.getName() + ".yml");
			File configFile = getFile("Worlds" + File.separator + world.getName() + ".yml");
			File backupFile = new File(Likeaboss.in.getDataFolder().getPath(), "old_files" + File.separator +  "v" + configVersion + File.separator + "Worlds" + File.separator + world.getName() + ".yml");
			
			((File) new File(backupFile.getParent())).mkdirs();
			
			try {
				Utility.fileToFile(configFile, backupFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Likeaboss.l.info("[Likeaboss] World " + world.getName() + "config backup created");
			
			
			if(Utility.isOlderVersion(configVersion, "2.0")){
				Likeaboss.l.info("[Likeaboss] Updating " + world.getName() + ".yml");
				try {
					PrintWriter stream = new PrintWriter(new BufferedWriter(new FileWriter(getFile("Worlds" + File.separator + world.getName() + ".yml"), true)));
					stream.println();
					stream.println();
					stream.println("version: " + latestVersion);
					
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Likeaboss.l.info("[Likeaboss] World " + world.getName() + "config updated");
			}
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
		return ((File) new File(Likeaboss.in.getDataFolder().getPath() + "/" + name));
	}
}
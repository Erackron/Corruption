package com.mcdr.likeaboss.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
		YamlConfiguration ability = getYamlConfig("plugins/Likeaboss/abilities.yml");
		
		if(ability == null)
			return;
		
		String configVersion;
		if(ability.isSet("version"))
			configVersion = ability.getString("version");
		else{
			configVersion = "1.7.0";
		}
		if(!isOlderVersion(latestVersion, configVersion))
			return;
		
		Likeaboss.l.info("[Likeaboss] Creating backup of abilities.yml!");
		File configFile = new File("plugins/Likeaboss/abilities.yml");
		File backupFile = new File("plugins/Likeaboss", "old_files" + File.separator + "v" + configVersion + File.separator + "abilities.yml");
		
		((File) new File(backupFile.getParent())).mkdirs();
		
		copy(configFile, backupFile);
		
		Likeaboss.l.info("[Likeaboss] Ability config backup created");
		
		if(isOlderVersion("2.0", configVersion)){
			Likeaboss.l.info("[Likeaboss] Updating abilities.yml");
			try {
				PrintWriter stream = new PrintWriter(new BufferedWriter(new FileWriter("plugins/Likeaboss/abilities.yml", true)));
				stream.println();
				stream.println();
				stream.println("# Do not touch this variable");
				stream.println("version: " + latestVersion);
				
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Likeaboss.l.info("[Likeaboss] Ability config updated");
		}
	}
	
	private void updateGlobalConfig(){
		YamlConfiguration global = getYamlConfig("plugins/Likeaboss/config.yml");
		
		if(global == null)
			return;
		
		String configVersion;
		if(global.isSet("version"))
			configVersion = global.getString("version");
		else{
			configVersion = "1.7.0";
		}
		if(!isOlderVersion(latestVersion, configVersion))
			return;
		
		Likeaboss.l.info("[Likeaboss] Creating backup of config.yml");
		File configFile = new File("plugins/Likeaboss/config.yml");
		File backupFile = new File("plugins/Likeaboss", "old_files" + File.separator + "v" + configVersion + File.separator + "config.yml");
		
		((File) new File(backupFile.getParent())).mkdirs();
		
		copy(configFile, backupFile);
		Likeaboss.l.info("[Likeaboss] Global config backup created");
		
		if(isOlderVersion("2.0", configVersion)){
			Likeaboss.l.info("[Likeaboss] Updating config.yml");
			global.set("Boss.Immunity", null);
			try {
				global.save("plugins/Likeaboss/config.yml");
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				PrintWriter stream = new PrintWriter(new BufferedWriter(new FileWriter("plugins/Likeaboss/config.yml", true)));
				stream.println();
				stream.println();
				stream.println("# Do not touch this variable!");
				stream.println("version: " + latestVersion);
				
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Likeaboss.l.info("[Likeaboss] Global config updated");
		}
	}
	
	private void updateBossConfig(){
		YamlConfiguration bosses = getYamlConfig("plugins/Likeaboss/bosses.yml");
		
		if(bosses == null)
			return;
		
		String configVersion;
		if(bosses.isSet("version"))
			configVersion = bosses.getString("version");
		else{
			configVersion = "1.7.0";
		}
		if(!isOlderVersion(latestVersion, configVersion))
			return;
		
		Likeaboss.l.info("[Likeaboss] Creating backup of bosses.yml");
		
		File configFile = new File("plugins/Likeaboss/bosses.yml");
		File backupFile = new File("plugins/Likeaboss", "old_files" + File.separator + "v" + configVersion + File.separator + "bosses.yml");
		
		((File) new File(backupFile.getParent())).mkdirs();
		
		copy(configFile, backupFile);
		Likeaboss.l.info("[Likeaboss] Bosses config backup created");
				
		if(isOlderVersion("2.0", configVersion)){
			Likeaboss.l.info("[Likeaboss] Updating bosses.yml");
			YamlConfiguration global = getYamlConfig("plugins/Likeaboss/config.yml");
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
				
				bosses.set(node + ".Immunity", immunity.getValues(true));				
			}			
			
			try {
				bosses.save("plugins/Likeaboss/bosses.yml");				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				PrintWriter stream = new PrintWriter(new BufferedWriter(new FileWriter("plugins/Likeaboss/bosses.yml", true)));
				stream.println();
				stream.println();
				stream.println("# Do not touch this variable!");
				stream.println("version: " + latestVersion);
				
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
			Likeaboss.l.info("[Likeaboss] Bosses config updated");
		}
	}
	
	private void updateEquipmentConfig(){
		YamlConfiguration equipment = getYamlConfig("plugins/Likeaboss/equipment.yml");
		
		if(equipment == null)
			return;
		
		String configVersion;
		if(equipment.isSet("version"))
			configVersion = equipment.getString("version");
		else{
			configVersion = "1.7.0";
		}
		if(!isOlderVersion(latestVersion, configVersion))
			return;
		
		Likeaboss.l.info("[Likeaboss] Creating backup of equipment.yml");
		File configFile = new File("plugins/Likeaboss/equipment.yml");
		File backupFile = new File("plugins/Likeaboss", "old_files" + File.separator + "v" + configVersion + File.separator + "equipment.yml");
		
		((File) new File(backupFile.getParent())).mkdirs();
		
		copy(configFile, backupFile);
		Likeaboss.l.info("[Likeaboss] Equipment config backup created");
		
		if(isOlderVersion("2.0", configVersion)){
			Likeaboss.l.info("[Likeaboss] Updating equipment.yml");
			try {
				PrintWriter stream = new PrintWriter(new BufferedWriter(new FileWriter("plugins/Likeaboss/equipment.yml", true)));
				stream.println();
				stream.println();
				stream.println("# Do not touch this variable!");
				stream.println("version: " + latestVersion);
				
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Likeaboss.l.info("[Likeaboss] Equipment config updated");
		}
	}
	
	private void updateMagicSpellsConfig(){
		YamlConfiguration magicSpells = getYamlConfig("plugins/Likeaboss/magicspells.yml");
		
		if(magicSpells == null)
			return;
		
		String configVersion;
		if(magicSpells.isSet("version"))
			configVersion = magicSpells.getString("version");
		else{
			configVersion = "1.7.0";
		}
		if(!isOlderVersion(latestVersion, configVersion))
			return;
		
		Likeaboss.l.info("[Likeaboss] Creating backup of magicspells.yml");
		File configFile = new File("plugins/Likeaboss/magicspells.yml");
		File backupFile = new File("plugins/Likeaboss", "old_files" + File.separator + "v" + configVersion + File.separator + "magicspells.yml");
		
		((File) new File(backupFile.getParent())).mkdirs();
		
		copy(configFile, backupFile);
		Likeaboss.l.info("[Likeaboss] MagicSpells config backup created");
		
		if(isOlderVersion("2.0", configVersion)){
			Likeaboss.l.info("[Likeaboss] Updating magicspells.yml");
			try {
				PrintWriter stream = new PrintWriter(new BufferedWriter(new FileWriter("plugins/Likeaboss/magicspells.yml", true)));
				stream.println();
				stream.println();
				stream.println("# Do not touch this variable!");
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
			YamlConfiguration worldConfig = getYamlConfig("plugins/Likeaboss/Worlds/" + world.getName() + ".yml");
			
			if(worldConfig == null)
				continue;
			
			String configVersion;
			if(worldConfig.isSet("version"))
				configVersion = worldConfig.getString("version");
			else{
				configVersion = "1.7.0";
			}
			if(!isOlderVersion(latestVersion, configVersion))
				continue;
			
			Likeaboss.l.info("[Likeaboss] Creating backup of " + world.getName() + ".yml");
			File configFile = new File("plugins/Likeaboss/Worlds/" + world.getName() + ".yml");
			File backupFile = new File("plugins/Likeaboss", "old_files" + File.separator +  "v" + configVersion + File.separator + "Worlds" + File.separator + world.getName() + ".yml");
			
			((File) new File(backupFile.getParent())).mkdirs();
			
			copy(configFile, backupFile);
			Likeaboss.l.info("[Likeaboss] World " + world.getName() + "config backup created");
			
			
			if(isOlderVersion("2.0", configVersion)){
				Likeaboss.l.info("[Likeaboss] Updating " + world.getName() + ".yml");
				try {
					PrintWriter stream = new PrintWriter(new BufferedWriter(new FileWriter("plugins/Likeaboss/Worlds/" + world.getName() + ".yml", true)));
					stream.println();
					stream.println();
					stream.println("# Do not touch this variable!");
					stream.println("version: " + latestVersion);
					
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Likeaboss.l.info("[Likeaboss] World " + world.getName() + "config updated");
			}
		}
	}
	
	private YamlConfiguration getYamlConfig(String path){
		File file = new File(path);
		YamlConfiguration yamlConfig = new YamlConfiguration();
		
		try {
			yamlConfig.load(file);
		} catch (Exception e) {
			return null;
		}
		
		return yamlConfig;		
	}
	
	/**
	 * Check if a version number is older than the current plugin version number
	 * @param pluginVer the current plugin version
	 * @param checkVer the version to check
	 * @return true if the version number is older
	 */
	private boolean isOlderVersion(String pluginVer, String checkVer) {
		return LabUpdateChecker.isNewerVersion(pluginVer, checkVer) && pluginVer.equals(checkVer);
	}
      
	
	private void copy(File f1, File f2) {
		InputStream in = null;
		try {
			in = new FileInputStream(f1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		copy(in, f2);
	}

	private void copy(InputStream in, File file) {
		// Make sure the input isn't null
		if(in == null)
			return;
		
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
}
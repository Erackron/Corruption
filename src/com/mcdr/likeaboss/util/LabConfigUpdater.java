package com.mcdr.likeaboss.util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mcdr.likeaboss.Likeaboss;

public class LabConfigUpdater {
	
	private String latestVersion;
	
	public LabConfigUpdater(){
		latestVersion = Likeaboss.in.getDescription().getVersion();
	}
	
	public void updateFiles(){
		updateAbilityConfig();
		updateGlobalConfig();
		updateBossConfig();
		updateEquipmentConfig();
		updateMagicSpellsConfig();
		updateWorldConfig();
	}
	
	private void updateAbilityConfig(){
		YamlConfiguration ability = getYamlConfig("plugins/Likeaboss/abilities.yml");
		
		if(ability == null)
			return;
		
		String configVersion = ability.getString("version");
		if(configVersion == null)
			configVersion = "1.7.0";
		if(!isOlderVersion(latestVersion, configVersion))
			return;		
	}
	
	private void updateGlobalConfig(){
		YamlConfiguration global = getYamlConfig("plugins/Likeaboss/config.yml");
		
		if(global == null)
			return;
		
		String configVersion = global.getString("version");
		if(configVersion == null)
			configVersion = "1.7.0";
		if(!isOlderVersion(latestVersion, configVersion))
			return;		
	}
	
	private void updateBossConfig(){
		YamlConfiguration bosses = getYamlConfig("plugins/Likeaboss/bosses.yml");
		
		if(bosses == null)
			return;
		
		String configVersion = bosses.getString("version");
		if(configVersion == null)
			configVersion = "1.7.0";
		if(!isOlderVersion(latestVersion, configVersion))
			return;
		
		if(isOlderVersion("1.7.1", configVersion)){
			for(String node : bosses.getKeys(false)){
				node += ".Spawn";
				String spawnString = bosses.getString(node);
				if (spawnString == null) {
					Likeaboss.l.warning("[Likeaboss] '" + node + "' in bosses config file is missing.");
					return;
				}
				
				String[] spawnValues = spawnString.split(" ");
				
				if (spawnValues.length < 2) {
					Likeaboss.l.warning("[Likeaboss] Missing values for '" + node + "' in bosses config file");
					return;
				} else if (spawnValues.length < 3){
					String[] temp = new String[spawnValues.length + 1];
				    System.arraycopy(spawnValues, 0, temp, 0, spawnValues.length);
				    temp[spawnValues.length] = "256";
				    spawnValues = temp;
				}
				
				bosses.set(node, null);
				
				bosses.createSection(node);
				
				bosses.set(node + ".Probability", Double.parseDouble(spawnValues[0]));
				bosses.set(node + ".SpawnerProbability", Double.parseDouble(spawnValues[1]));
				bosses.set(node + ".MaxSpawnHeight", Integer.parseInt(spawnValues[2]));
				
				bosses.createSection("version");
				bosses.set("version", "1.7.1");
				
				try {
					bosses.save("plugins/Likeaboss/bosses.yml");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void updateEquipmentConfig(){
		YamlConfiguration equipment = getYamlConfig("plugins/Likeaboss/equipment.yml");
		
		if(equipment == null)
			return;
		
		String configVersion = equipment.getString("version");
		if(configVersion == null)
			configVersion = "1.7.0";
		if(!isOlderVersion(latestVersion, configVersion))
			return;	
	}
	
	private void updateMagicSpellsConfig(){
		YamlConfiguration magicSpells = getYamlConfig("plugins/Likeaboss/magicspells.yml");
		
		if(magicSpells == null)
			return;
		
		String configVersion = magicSpells.getString("version");
		if(configVersion == null)
			configVersion = "1.7.0";
		if(!isOlderVersion(latestVersion, configVersion))
			return;				
	}
	
	private void updateWorldConfig(){
		for(World world : Bukkit.getServer().getWorlds()){
			YamlConfiguration worldConfig = getYamlConfig("plugins/Likeaboss/Worlds/" + world.getName() + ".yml");
			
			if(worldConfig == null)
				continue;
			
			String configVersion = worldConfig.getString("version");
			if(configVersion == null)
				configVersion = "1.7.0";
			if(!isOlderVersion(latestVersion, configVersion))
				continue;			
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
        String s1 = normalisedVersion(pluginVer);
        String s2 = normalisedVersion(checkVer);
        int cmp = s1.compareTo(s2);
        return (cmp > 0);
    }
	
	/**
	 * Normalize a version number (String)
	 * @param ver version number
	 * @return normalized version number
	 */
	private String normalisedVersion(String ver) {
        return normalisedVersion(ver, ".", 3);
    }

	/**
	 * Normalize a version number (String)
	 * @param ver version number
	 * @param sep seperation character
	 * @param maxWidth max width
	 * @return normalized version number
	 */
	private String normalisedVersion(String ver, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(ver);
        StringBuilder sb = new StringBuilder();
        
        for (String s : split)
            sb.append(String.format("%" + maxWidth + 's', s));
        
        return sb.toString();
    }
	
}

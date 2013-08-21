package com.mcdr.corruption.config;

import java.io.File;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mcdr.corruption.util.CorLogger;


public class MagicSpellsConfig extends BaseConfig {
	
	public static List<String> enabledSpells;
	public static List<String> disabledSpells;
	public static boolean allEnabled;
	public static boolean useWhitelist;
	public static String castFailMsg;

	public static void Load(){
		File file = new File(DATAFOLDER, "magicspells.yml");
		
		if (!file.exists())
			copyResource(file, "com/mcdr/corruption/config/magicspells.yml");
		
		LoadMagicSpells(loadConfig(file));
	}
	
	private static void LoadMagicSpells(YamlConfiguration yamlConfig){
		enabledSpells = yamlConfig.getStringList("whitelist");
		disabledSpells = yamlConfig.getStringList("blacklist");
		allEnabled = yamlConfig.getBoolean("enable-all-spells", false);
		useWhitelist = yamlConfig.getBoolean("whitelist", true);
		
		
		if(!allEnabled){
			if (useWhitelist && enabledSpells.isEmpty()) {
				CorLogger.w("'whitelist' in MagicSpells config file is empty.");
			} else if(!(useWhitelist) && disabledSpells.isEmpty()){
				CorLogger.w("'blacklist' in MagicSpells config is empty.");
			} 
						
		}
	}
	
	public List<String> getEnabledSpells(){
		return enabledSpells;
	}
	
	public List<String> getDisabledSpells(){
		return disabledSpells;
	}
	
	public boolean allEnabled(){
		return allEnabled;
	}
	
	public boolean useWhitelist(){
		return useWhitelist;
	}
	
}

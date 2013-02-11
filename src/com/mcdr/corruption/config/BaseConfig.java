package com.mcdr.corruption.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.util.Utility;

public abstract class BaseConfig {
	protected final static char SEPERATOR = File.pathSeparatorChar;
	protected final static String DATAFOLDER = Corruption.in.getDataFolder().getPath();
	
	protected static void copyResource(File file, String resourcePath) {
		InputStream inputStream = Corruption.in.getResource(resourcePath);

		if (inputStream == null) {
			Corruption.l.severe("["+Corruption.in.getName()+"] Missing resource file: '" + resourcePath + "', please notify the plugin author");
			Bukkit.getPluginManager().disablePlugin(Corruption.in);
		}
		else {
			Corruption.l.info("["+Corruption.in.getName()+"] Creating default config file: " + file.getName());

			try {
				file.getParentFile().mkdirs();
				Utility.streamToFile(inputStream, file);
			} catch (Exception e) {
				e.printStackTrace();
				Bukkit.getPluginManager().disablePlugin(Corruption.in);
			}
		}
	}
		
	protected static YamlConfiguration loadConfig(File file) {
		YamlConfiguration yamlConfig = new YamlConfiguration();
		
		try {
			yamlConfig.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return yamlConfig;
	}
	
	protected static void saveConfig(YamlConfiguration yamlConfig, String fileName){
		saveConfig(yamlConfig, fileName, false);
	}
	
	protected static void saveConfig(YamlConfiguration yamlConfig, String fileName, boolean inWorldsFolder){
		try{
			yamlConfig.save(new File(DATAFOLDER + SEPERATOR + (inWorldsFolder?"Worlds" + SEPERATOR:"") + fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

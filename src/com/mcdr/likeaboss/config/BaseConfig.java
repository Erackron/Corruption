package com.mcdr.likeaboss.config;

import java.io.File;
import java.io.InputStream;

import org.bukkit.configuration.file.YamlConfiguration;

import com.mcdr.likeaboss.Likeaboss;
import com.mcdr.likeaboss.utility.Utility;


public abstract class BaseConfig {
	protected static File LoadFile(String filePath, String resourcePath) {
		File file = new File(filePath);
		
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			
			try {
				InputStream inputStream = Likeaboss.in.getResource(resourcePath);
				
				if (inputStream == null) {
					Likeaboss.l.severe("[Likeaboss] Missing ressource file: '" + resourcePath + "', please notify the plugin author");
					return null;
				}
				else {
					Likeaboss.l.info("[Likeaboss] Creating default config file: " + file.getName());
					Utility.StreamToFile(inputStream, file);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return file;
	}
	
	protected static YamlConfiguration LoadConfig(File file) {
		YamlConfiguration yamlConfig = new YamlConfiguration();
		
		try {
			yamlConfig.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return yamlConfig;
	}
}

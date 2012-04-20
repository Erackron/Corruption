package cam.config;

import java.io.File;
import java.io.InputStream;

import org.bukkit.configuration.file.YamlConfiguration;

import cam.Likeaboss;
import cam.Utility;

public abstract class BaseConfig {
	protected static File LoadFile(String filePath, String ressourcePath) {
		File file = new File(filePath);
		
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			
			try {
				InputStream inputStream = Likeaboss.instance.getResource(ressourcePath);
				
				if (inputStream == null) {
					Likeaboss.logger.severe("[Likeaboss] Missing ressource file: '" + ressourcePath + "', please notify the plugin author");
					return null;
				}
				else {
					Likeaboss.logger.info("[Likeaboss] Creating default config file: " + file.getName());
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

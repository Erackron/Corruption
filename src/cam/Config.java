package cam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
	
	private FileConfiguration config = null;
	private String filePath = "plugins/Likeaboss/config.yml";
	private String[] strings = {"Boss.Chance", "Boss.HealthCoef", "Boss.DamageCoef", "Boss.ExpCoef"};
	private Map<String, Double> lines = new HashMap<String, Double>();
	
	public Config() {
	}
	
	public void LoadFile(Likeaboss plugin) {
		config = plugin.getConfig();
		File file = new File(filePath);
		
		if (!file.exists())
			CreateFile();
		
		try {
			config.load(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

		for (int i = 0 ; i < strings.length ; i++) {
			if (!config.contains(strings[i])) {
				i = -1;
				CreateFile();
				continue;
			}
			lines.put(strings[i], config.getDouble(strings[i]));
		}
	}

	public void CreateFile() {
		config.set("Boss.Chance", 3.5);
		config.set("Boss.HealthCoef", 6);
		config.set("Boss.DamageCoef", 3);
		config.set("Boss.ExpCoef", 7.5);
		
		try {
			config.save(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public FileConfiguration getFile() {
		return config;
	}
	
	public double getValue(String string) {
		return lines.get(string);
	}
}

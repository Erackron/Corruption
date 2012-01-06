package cam.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import cam.Likeaboss;

public class LabConfig {
	
	private FileConfiguration configFile = null;
	private String filePath = "plugins/Likeaboss/config.yml";
	
	public static enum Entry {
		BOSS_SPAWN_CHANCE ("Boss.Spawn.Chance", 4.0),
		BOSS_SPAWN_MAXHEIGHT ("Boss.Spawn.MaxHeight", 128.0),
		BOSS_STATS_HEALTHCOEF ("Boss.Stats.HealthCoef", 5.0),
		BOSS_STATS_DAMAGECOEF ("Boss.Stats.DamageCoef", 3.0),
		BOSS_STATS_EXPCOEF ("Boss.Stats.ExpCoef", 8.0),
		TASK_CHECKENTITYHEALTH ("Task.CheckEntityHealth", 1.5),
		TASK_CHECKENTITYEXISTENCE ("Task.CheckEntityExistence", 5.0);
		
		private String line;
		private double value;
		
		private Entry(String line, double value) {
			this.line = line;
			this.value = value;
		}
		
		public String getLine() {
			return line;
		}
		
		public double getValue() {
			return value;
		}
		
		public void setValue(double value) {
			this.value = value;
		}
	}
	
	public LabConfig() {
	}
	
	public void LoadFile(Likeaboss plugin) {
		configFile = plugin.getConfig();
		File file = new File(filePath);
		
		if (!file.exists()) {
			Likeaboss.log.warning("[Likeaboss] Creating default config file");
			CreateFile();
		}
		
		try {
			configFile.load(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		for (Entry entry : Entry.values()) {
			if (!configFile.contains(entry.getLine())) {
				Likeaboss.log.warning("[Likeaboss] Config file incomplete");
				Likeaboss.log.warning("[Likeaboss] Adding missing data");
				CreateFile();
			}
			entry.setValue(configFile.getDouble(entry.getLine()));
		}
	}

	public void CreateFile() {
		for (Entry entry : Entry.values()) {
			if (!configFile.contains(entry.getLine()))
				configFile.set(entry.getLine(), entry.getValue());
		}
		
		try {
			configFile.save(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

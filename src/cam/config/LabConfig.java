package cam.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import cam.Likeaboss;
import cam.boss.DropManager;

public class LabConfig {
	
	private FileConfiguration configFile = null;
	private String filePath = "plugins/Likeaboss/config.yml";
	private DropManager dropManager = null;
	
	public static enum BossesData {
		SPAWN_CHANCE ("Boss.Spawn.Chance", 4.0),
		SPAWN_MAXHEIGHT ("Boss.Spawn.MaxHeight", 128.0),
		SPAWN_FROMMOBSPAWNER ("Boss.Spawn.FromMobSpawner", 0.0),
		STATS_HEALTHCOEF ("Boss.Stats.HealthCoef", 5.0),
		STATS_DAMAGECOEF ("Boss.Stats.DamageCoef", 3.0),
		STATS_EXPCOEF ("Boss.Stats.ExpCoef", 8.0);
		
		private String line = null;
		private double value = 0;
		
		private BossesData(String line, double value) {
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
	
	public static enum DropsData {
		
		FIRSTROLL_FIRSTITEM ("Drops.FirstRoll.FirstItem", 0, 0, 0, 0, 0),
		FIRSTROLL_SECONDITEM ("Drops.FirstRoll.SecondItem", 0, 0, 0, 0, 0),
		FIRSTROLL_THIRDITEM ("Drops.FirstRoll.ThirdItem", 0, 0, 0, 0, 0),
		FIRSTROLL_FOURTHITEM ("Drops.FirstRoll.FourthItem", 0, 0, 0, 0, 0),
		
		SECONDROLL_FIRSTITEM ("Drops.SecondRoll.FirstItem", 1, 0, 0, 0, 0),
		SECONDROLL_SECONDITEM ("Drops.SecondRoll.SecondItem", 1, 0, 0, 0, 0),
		SECONDROLL_THIRDITEM ("Drops.SecondRoll.ThirdItem", 1, 0, 0, 0, 0),
		SECONDROLL_FOURTHITEM ("Drops.SecondRoll.FourthItem", 1, 0, 0, 0, 0),
		
		THIRDROLL_FIRSTITEM ("Drops.ThirdRoll.FirstItem", 2, 0, 0, 0, 0),
		THIRDROLL_SECONDITEM ("Drops.ThirdRoll.SecondItem", 2, 0, 0, 0, 0),
		THIRDROLL_THIRDITEM ("Drops.ThirdRoll.ThirdItem", 2, 0, 0, 0, 0),
		THIRDROLL_FOURTHITEM ("Drops.ThirdRoll.FourthItem", 2, 0, 0, 0, 0);
		
		private String line = null;
		private int rollId = 0;
		
		private Map<String, Integer> params = new LinkedHashMap<String, Integer>();
		
		private DropsData(String line, int rollId, int materialId, int chance, int quantityMin, int quantityMax) {
			this.line = line;
			this.rollId = rollId;
			params.put("MaterialId", materialId);
			params.put("Chance", chance);
			params.put("QuantityMin", quantityMin);
			params.put("QuantityMax", quantityMax);
		}

		public String getLine() {
			return line;
		}
		
		public int getRollId() {
			return rollId;
		}
		
		public String getStringValues() {
			String values = "";
			
			for (Entry<String, Integer> entry : params.entrySet())
				values += String.valueOf(entry.getValue()) + ' ';
			values = values.substring(0, values.length() - 1);
			
			return values;
		}
		
		public Map<String, Integer> getValues() {
			return params;
		}
	}
	
	public static enum TasksData {
		BOSS_VISUAL_EFFECT ("Task.VisualEffect", 1.0),
		CHECK_BOSS_PROXIMITY ("Task.CheckBossProximity", 0.5),
		CHECK_ENTITY_HEALTH ("Task.CheckEntityHealth", 2.0),
		CHECK_ENTITY_EXISTENCE ("Task.CheckEntityExistence", 5.0);
		
		private String line = null;
		private double value = 0;
		
		private TasksData(String line, double value) {
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
	
	public LabConfig(DropManager dropManager) {
		this.dropManager = dropManager;
	}
	
	public void LoadFile(Likeaboss plugin) {
		File file = new File(filePath);
		dropManager.getRolls().clear();
		boolean needSaving = false;
		
		configFile = plugin.getConfig();
		
		if (!file.exists()) {
			Likeaboss.log.warning("[Likeaboss] Creating default config file.");
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
				
		//Boss parameters
		for (BossesData bossData : BossesData.values()) {
			String line = bossData.getLine();
			
			if (!configFile.contains(line)) {
				Likeaboss.log.warning("[Likeaboss] Adding missing data in config file.");
				
				configFile.set(line, bossData.getValue());
				needSaving = true;
				continue;
			}
			
			bossData.setValue(configFile.getDouble(line));
		}
		
		//Drops parameters
		for (DropsData dropData : DropsData.values()) {
			String line = dropData.getLine();
			
			if (!configFile.contains(line)) {
				Likeaboss.log.warning("[Likeaboss] Adding missing data in config file.");
				
				configFile.set(line, dropData.getStringValues());
				needSaving = true;
				continue;
			}
			
			String stringValues = configFile.getString(line);
			
			//Check the string before splitting
			boolean valid = true;
			for (int i = 0 ; i < stringValues.length() ; i++) {
				char c = stringValues.charAt(i);
				if (!Character.isDigit(c) && c != ' ') {
					valid = false;
					break;
				}
			}
			if (!valid) {
				Likeaboss.log.warning("[Likeaboss] Invalid args for '" + line + "'. Using default values.");
				continue;
			}
			
			//Split and convert to int
			String[] valuesSplitted = stringValues.split(" ");
			int[] values = new int[valuesSplitted.length];
			
			for (int i = 0 ; i < valuesSplitted.length ; i++)
				values[i] = Integer.valueOf(valuesSplitted[i]);
			
			//Check if missing argument
			if (values.length < dropData.getValues().size()) {
				Likeaboss.log.warning("[Likeaboss] Invalid args for '" + line + "'. Using default values.");
				continue;
			}
			
			if (values[0] != 0)
				dropManager.AddPossibleDrop(dropData.getRollId(), values);
		}
		
		//Tasks parameters
		for (TasksData taskData : TasksData.values()) {
			String line = taskData.getLine();
			
			if (!configFile.contains(line)) {
				Likeaboss.log.warning("[Likeaboss] Adding missing data in config file.");
				
				configFile.set(line, taskData.getValue());
				needSaving = true;
				continue;
			}
			
			taskData.setValue(configFile.getDouble(line));
		}
		
		if (needSaving)
			SaveFile();
	}
	
	private void CreateFile() {
		for (BossesData bossData : BossesData.values())
			configFile.set(bossData.getLine(), bossData.getValue());
		
		for (DropsData dropData : DropsData.values())
			configFile.set(dropData.getLine(), dropData.getStringValues());
		
		for (TasksData taskData : TasksData.values())
			configFile.set(taskData.getLine(), taskData.getValue());
		
		SaveFile();
	}
	
	private void SaveFile() {
		try {
			configFile.save(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

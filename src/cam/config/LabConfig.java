package cam.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;


import cam.Likeaboss;
import cam.boss.BossManager;
import cam.boss.DropManager;

public class LabConfig {
	
	private List<World> worlds = new ArrayList<World>();
	private YamlConfiguration configFile = new YamlConfiguration();
	private BossManager bossManager = null;
	private DropManager dropManager = null;
	
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
		
	public LabConfig(Likeaboss plugin) {
		this.worlds = plugin.getServer().getWorlds();
		this.bossManager = plugin.getBossManager();
		this.dropManager = plugin.getDropManager();
	}
	
	public void LoadFiles() {
		dropManager.getRolls().clear();
		bossManager.getBossesParams().clear();
		
		LoadGlobalConfigFile();
		LoadWorldConfigFiles();
	}
	
	private void LoadGlobalConfigFile() {
		String filePath = "plugins/Likeaboss/config.yml";
		File file = new File(filePath);
		
		if (!file.exists()) {
			Likeaboss.log.warning("[Likeaboss] Creating default global config file.");
			CreateGlobalConfigFile(filePath);
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
		
		boolean needSaving = false;
		
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
			SaveFile(filePath);
	}
	
	private void LoadWorldConfigFiles() {
		Iterator<World> it = worlds.iterator();
		
		while (it.hasNext()) {
			World world = it.next();
			
			String filePath = "plugins/Likeaboss/" + world.getName() + "/config.yml";
			File file = new File(filePath);
			
			if (!file.exists()) {
				Likeaboss.log.warning("[Likeaboss] Creating default config file for " + world + ".");
				CreateWorldConfigFile(filePath);
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
			
			boolean needSaving = false;
			
			//Boss parameters
			for (BossesData bossData : BossesData.values()) {
				String spawnLine = bossData.getSpawnData().getLine();
				String statsLine = bossData.getStatsData().getLine();
				
				if (!configFile.contains(spawnLine) || !configFile.contains(statsLine)) {
					Likeaboss.log.warning("[Likeaboss] Adding missing data in config file.");
					
					configFile.set(spawnLine, bossData.getSpawnData().getStringValues());
					configFile.set(statsLine, bossData.getStatsData().getStringValues());
					needSaving = true;
					continue;
				}
				
				String stringSpawnValues = configFile.getString(spawnLine);
				String stringStatsValues = configFile.getString(statsLine);
				
				double[] spawnValues = ProcessString(stringSpawnValues);
				if (spawnValues == null)
					continue;
				
				double[] statsValues = ProcessString(stringStatsValues);
				if (statsValues == null)
					continue;
				
				//Check if missing argument
				if (spawnValues.length < bossData.getSpawnData().getValues().size()) {
					Likeaboss.log.warning("[Likeaboss] Invalid args for '" + spawnLine + "'. Ignoring.");
					continue;
				}
				if (statsValues.length < bossData.getStatsData().getValues().size()) {
					Likeaboss.log.warning("[Likeaboss] Invalid args for '" + statsLine + "'. Ignoring.");
					continue;
				}
				
				bossManager.AddBossParameters(world, bossData.getType(), spawnValues, statsValues);
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
				
				double[] values = ProcessString(stringValues);
				if (values == null)
					continue;
				
				//Check if missing argument
				if (values.length < dropData.getValues().size()) {
					Likeaboss.log.warning("[Likeaboss] Invalid args for '" + line + "'. Ignoring.");
					continue;
				}
				
				if (values[0] != 0) //Air = no drop
					dropManager.AddPossibleDrop(dropData.getRollId(), values);
			}
			
			if (needSaving)
				SaveFile(filePath);
		}
	}
	
	private double[] ProcessString(String string) {
		//Check the string before splitting
		for (int i = 0 ; i < string.length() ; i++) {
			char c = string.charAt(i);
			
			if (!Character.isDigit(c) && c != ' ' && c != '.') {
				Likeaboss.log.warning("[Likeaboss] Invalid args for '" + string + "'. Ignoring.");
				return null;
			}
		}
		
		//Split
		String[] splitted = string.split(" ");
		double[] values = new double[splitted.length];
			
		for (int i = 0 ; i < splitted.length ; i++)
			values[i] = Double.valueOf(splitted[i]);
			
		return values;
	}
	
	private void CreateGlobalConfigFile(String filePath) {
		configFile = new YamlConfiguration();
		
		for (TasksData taskData : TasksData.values())
			configFile.set(taskData.getLine(), taskData.getValue());
		
		SaveFile(filePath);
	}
	
	private void CreateWorldConfigFile(String filePath) {
		configFile = new YamlConfiguration();
		
		for (BossesData bossData : BossesData.values()) {
			configFile.set(bossData.getSpawnData().getLine(), bossData.getSpawnData().getStringValues());
			configFile.set(bossData.getStatsData().getLine(), bossData.getStatsData().getStringValues());
		}
		
		for (DropsData dropData : DropsData.values())
			configFile.set(dropData.getLine(), dropData.getStringValues());
		
		SaveFile(filePath);
	}
	
	private void SaveFile(String filePath) {
		try {
			configFile.save(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

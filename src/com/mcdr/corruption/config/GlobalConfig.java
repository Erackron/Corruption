package com.mcdr.corruption.config;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlConfiguration;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.util.CorLogger;
import com.mcdr.corruption.util.CorLogger.LogLevel;


public abstract class GlobalConfig extends BaseConfig {
	public static boolean checkUpdateOnStartup = true;
	public static boolean reloadAfterUpdating = true;
	private static YamlConfiguration yamlConfig = null;
	
	public enum CommandParam {
		IGNORE_DELAY (120) {@Override public String getNode() {return "Command.Ignore.Delay";}},
		SPAWN_MAX (50) {@Override public String getNode() {return "Command.Spawn.Max";}};
		
		private int value;
		
		private CommandParam(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
		public abstract String getNode();
		
		public void setValue(int value) {
			this.value = value;
		}
	}
	
	public enum MessageParam {
		PLAYER_FOUND_BOSS_1 ("&cYou attacked a Corrupted {BOSSNAME} boss!") {@Override public String getNode() {return "Message.PlayerFoundBoss.ToPlayer";}},
		PLAYER_FOUND_BOSS_2 ("&c{PLAYER} found a Corrupted {BOSSNAME}!") {@Override public String getNode() {return "Message.PlayerFoundBoss.ToOthers";}},
		BOSS_FOUND_PLAYER_1 ("&cA Corrupted {BOSSNAME} found you!") {@Override public String getNode() {return "Message.BossFoundPlayer.ToPlayer";}},
		BOSS_FOUND_PLAYER_2 ("&cA Corrupted {BOSSNAME} found {PLAYER}!") {@Override public String getNode() {return "Message.BossFoundPlayer.ToOthers";}},
		PROXIMITY ("&4You sense a magical disruption...") {@Override public String getNode() {return "Message.Proximity";}},
		CUSTOMBOSSNAME("Corrupted {BOSSNAME} &7[{HEALTH}/{MAXHEALTH}]") {@Override public String getNode() {return "Message.CustomBossName";}},
		VIEWERMESSAGE("Corrupted {BOSSNAME} Health: &7{HEALTH} (-{DAMAGE})") {@Override public String getNode() {return "Message.ViewerMessage";}},
		VIEWERDEFEATED("Corrupted {BOSSNAME} has been defeated."){@Override public String getNode() {return "Message.ViewerDefeated";}},
		VIEWERDAMAGEABSORBED("Corrupted {BOSSNAME}'s armour absorbed the damage."){@Override public String getNode() {return "Message.ViewerDamageAbsorbed";}};
		
		private String message;
		
		private MessageParam(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}
		
		public abstract String getNode();
		
		public void setMessage(String message) {
			this.message = message;
		}
	}
	
	public enum TaskParam {
		DRAW_BOSS_EFFECT (1.0) {@Override public String getNode() {return "Task.DrawBossEffect";}},
		CHECK_ENTITY_EXISTENCE (2.0) {@Override public String getNode() {return "Task.CheckEntityExistence";}},
		CHECK_ENTITY_PROXIMITY (0.5) {@Override public String getNode() {return "Task.CheckEntityProximity";}},
		SAVE_PLAYER_DATA (150.0) {@Override public String getNode() {return "Task.SavePlayerData";}},
		LOAD_PLAYER_DATA (5.0) {@Override public String getNode() {return "Task.LoadPlayerData";}};
		
		private double value;
		
		private TaskParam(double value) {
			this.value = value;
		}
		
		public double getValue() {
			return value;
		}
		
		public abstract String getNode();
		
		public void setValue(double value) {
			this.value = value;
		}
	}
	
	public enum BossParam {
		OVERWRITE_DROPS (false) {@Override public String getNode() {return "Boss.OverwriteDrops";}},
		ENABLE_BIOMES (false) {@Override public String getNode() {return "Boss.EnableBiomes";}},
		USE_HEALTH_AS_MULTIPLIER (true) {@Override public String getNode() {return "Boss.SetHealthAsMultiplier";}},
		USE_DAMAGE_AS_MULTIPLIER (true) {@Override public String getNode() {return "Boss.SetDamageAsMultiplier";}},
		USE_EXPERIENCE_AS_MULTIPLIER (true) {@Override public String getNode() {return "Boss.SetExperienceAsMultiplier";}};
		
		private boolean boolValue;
		
		private BossParam(boolean value){
			setValue(value);
		}		
		
		public boolean getValue(){
			return boolValue;
		}
		
		public abstract String getNode();
		
		public void setValue(boolean value) {
			this.boolValue = value;
		}
	}
	
	public static void load() {
		File file = new File(Corruption.in.getDataFolder().getPath(), "config.yml");
		boolean saveNeeded = false;
		
		if (!file.exists())
			copyResource(file, "com/mcdr/corruption/config/config.yml");
		
		yamlConfig = loadConfig(file);
		
		if(yamlConfig.isSet("CheckUpdateOnStartup"))
			checkUpdateOnStartup = yamlConfig.getBoolean("CheckUpdateOnStartup");
		else {
			yamlConfig.set("CheckUpdateOnStartup", true);
			saveNeeded = true;
		}
		
		if(yamlConfig.isSet("ReloadAfterUpdating"))
			checkUpdateOnStartup = yamlConfig.getBoolean("ReloadAfterUpdating");
		else {
			yamlConfig.set("ReloadAfterUpdating", true);
			saveNeeded = true;
			
		}
		
		if(yamlConfig.isSet("LogLevel")){
			Level logLevel;
			try{
				logLevel = LogLevel.parse(yamlConfig.getString("LogLevel").toUpperCase());
			} catch(IllegalArgumentException e){
				CorLogger.warning("Invalid LogLevel specified: '"+yamlConfig.getString("LogLevel").toUpperCase()+"' does not exist. Reverting to default: INFO");
				logLevel = LogLevel.INFO;
			}
			CorLogger.setLogLevel(logLevel);
		} else {
			yamlConfig.set("LogLevel", "INFO");
			saveNeeded = true;
		}
		
		saveNeeded = loadCommandParams(yamlConfig) || saveNeeded;
		saveNeeded = loadMessageParams(yamlConfig) || saveNeeded;
		saveNeeded = loadTaskParams(yamlConfig) || saveNeeded;
		saveNeeded = loadBossParams(yamlConfig) || saveNeeded;
		if(saveNeeded) save();
	}
	
	private static boolean loadCommandParams(YamlConfiguration yamlConfig) {
		boolean saveNeeded = false;
		for (CommandParam commandParam : CommandParam.values()) {
			String node = commandParam.getNode();
			
			if (!yamlConfig.contains(node)) {
				CorLogger.w("Adding '" + node + "' in config file.");
				yamlConfig.set(node, commandParam.getValue());
				saveNeeded = true;
				continue;
			}
			commandParam.setValue(yamlConfig.getInt(node));
		}
		return saveNeeded;
	}
	
	private static boolean loadMessageParams(YamlConfiguration yamlConfig) {
		boolean saveNeeded = false;
		for (MessageParam messageParam : MessageParam.values()) {
			String node = messageParam.getNode();
			
			if (!yamlConfig.contains(node)) {
				CorLogger.w("Adding '" + node + "' in config file.");
				yamlConfig.set(node, messageParam.getMessage());
				saveNeeded = true;
				continue;
			}
			messageParam.setMessage(yamlConfig.getString(node));
		}
		return saveNeeded;
	}
	
	private static boolean loadTaskParams(YamlConfiguration yamlConfig) {
		boolean saveNeeded = false;
		for (TaskParam taskParam : TaskParam.values()) {
			String node = taskParam.getNode();
				
			if (!yamlConfig.contains(node)) {
				CorLogger.w("Adding '" + node + "' in config file.");
				yamlConfig.set(node, taskParam.getValue());
				saveNeeded = true;
				continue;
			}
			taskParam.setValue(yamlConfig.getDouble(node));
		}
		return saveNeeded;
	}
	
	private static boolean loadBossParams(YamlConfiguration yamlConfig) {
		boolean saveNeeded = false;
		for (BossParam bossParam : BossParam.values()) {
			String node = bossParam.getNode();

			if (!yamlConfig.contains(node)) {
				CorLogger.w("Adding '" + node + "' in config file.");
				yamlConfig.set(node, bossParam.getValue());
				saveNeeded = true;
				continue;
			}
			bossParam.setValue(yamlConfig.getBoolean(node));
		}
		return saveNeeded;
	}
	
	public static void save(){
		yamlConfig.set("LogLevel", CorLogger.parseLogLevel().getName());
		saveConfig(yamlConfig, "config.yml");
	}
}

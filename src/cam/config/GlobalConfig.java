package cam.config;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import cam.Likeaboss;

public abstract class GlobalConfig extends BaseConfig {
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
		PLAYER_FOUND_BOSS_1 ("&cOh noes, that's a {BOSSNAME} boss!") {@Override public String getNode() {return "Message.PlayerFoundBoss.ToPlayer";}},
		PLAYER_FOUND_BOSS_2 ("&c{PLAYER} found a {BOSSNAME} boss!") {@Override public String getNode() {return "Message.PlayerFoundBoss.ToOthers";}},
		BOSS_FOUND_PLAYER_1 ("&cSneaky {BOSSNAME} boss.") {@Override public String getNode() {return "Message.BossFoundPlayer.ToPlayer";}},
		BOSS_FOUND_PLAYER_2 ("&cA {BOSSNAME} boss found {PLAYER}!") {@Override public String getNode() {return "Message.BossFoundPlayer.ToOthers";}},
		PROXIMITY ("&4You feel an evil presence...") {@Override public String getNode() {return "Message.Proximity";}},
		VIEWERMESSAGE("Boss Health: {HEALTH} (-{DAMAGE})") {@Override public String getNode() {return "Message.ViewerMessage";}},
		VIEWERDEFEATED("{BOSSNAME} boss has been defeated."){@Override public String getNode() {return "Message.ViewerDefeated";}};
		
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
		CHECK_ENTITY_HEALTH (2.0) {@Override public String getNode() {return "Task.CheckEntityHealth";}},
		CHECK_ENTITY_EXISTENCE (5.0) {@Override public String getNode() {return "Task.CheckEntityExistence";}},
		CHECK_ENTITY_PROXIMITY (0.5) {@Override public String getNode() {return "Task.CheckEntityProximity";}},
		SAVE_PLAYER_DATA (600.0) {@Override public String getNode() {return "Task.SavePlayerData";}};
		
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
		ATTACK_IMMUNE (false) {@Override public String getNode() {return "Boss.Immunity.Attack";}},
		PROJECTILE_IMMUNE (false) {@Override public String getNode() {return "Boss.Immunity.Projectile";}},
		BLOCK_EXPLOSION_IMMUNE (false) {@Override public String getNode() {return "Boss.Immunity.BlockExplosion";}},
		ENTITY_EXPLOSION_IMMUNE (false) {@Override public String getNode() {return "Boss.Immunity.EntityExplosion";}},
		FIRE_IMMUNE (true) {@Override public String getNode() {return "Boss.Immunity.Fire";}},
		LAVA_IMMUNE (true) {@Override public String getNode() {return "Boss.Immunity.Lava";}},
		ENCHANT_FIRETICK_IMMUNE (false) {@Override public String getNode() {return "Boss.Immunity.EnchantFireTick";}},
		ENVIRONMENTAL_FIRETICK_IMMUNE (true) {@Override public String getNode() {return "Boss.Immunity.EnvironmentalFireTick";}},
		FALL_IMMUNE (true) {@Override public String getNode() {return "Boss.Immunity.Fall";}},
		CONTACT_IMMUNE (true) {@Override public String getNode() {return "Boss.Immunity.Contact";}},
		DROWNING_IMMUNE (true) {@Override public String getNode() {return "Boss.Immunity.Drowning";}},
		LIGHTNING_IMMUNE (false) {@Override public String getNode() {return "Boss.Immunity.Lightning";}},
		SUFFOCATION_IMMUNE (true) {@Override public String getNode() {return "Boss.Immunity.Suffocation";}},
		MAGIC_IMMUNE (false) {@Override public String getNode() {return "Boss.Immunity.Magic";}},
		POISON_IMMUNE (false) {@Override public String getNode() {return "Boss.Immunity.Poison";}};
		
		private boolean value;
		
		private BossParam(boolean value) {
			this.value = value;
		}
		
		public boolean getValue() {
			return value;
		}
		
		public abstract String getNode();
		
		public void setValue(boolean value) {
			this.value = value;
		}
	}
	
	public static void Load() {
		File file = LoadFile("plugins/Likeaboss/config.yml", "cam/config/config.yml");
		
		if (file == null)
			return;
		
		YamlConfiguration yamlConfig = LoadConfig(file);
		
		LoadCommandParams(yamlConfig);
		LoadMessageParams(yamlConfig);
		LoadTaskParams(yamlConfig);
		LoadBossParams(yamlConfig);
	}
	
	private static void LoadCommandParams(YamlConfiguration yamlConfig) {
		for (CommandParam commandParam : CommandParam.values()) {
			String node = commandParam.getNode();
			
			if (!yamlConfig.contains(node)) {
				Likeaboss.logger.warning("[Likeaboss] Adding '" + node + "' in config file.");
				yamlConfig.set(node, commandParam.getValue());
				continue;
			}
			
			commandParam.setValue(yamlConfig.getInt(node));
		}
	}
	
	private static void LoadMessageParams(YamlConfiguration yamlConfig) {
		for (MessageParam messageParam : MessageParam.values()) {
			String node = messageParam.getNode();
			
			if (!yamlConfig.contains(node)) {
				Likeaboss.logger.warning("[Likeaboss] Adding '" + node + "' in config file.");
				yamlConfig.set(node, messageParam.getMessage());
				continue;
			}
			
			messageParam.setMessage(yamlConfig.getString(node));
		}
	}
	
	private static void LoadTaskParams(YamlConfiguration yamlConfig) {
		for (TaskParam taskParam : TaskParam.values()) {
			String node = taskParam.getNode();
				
			if (!yamlConfig.contains(node)) {
				Likeaboss.logger.warning("[Likeaboss] Adding '" + node + "' in config file.");
				yamlConfig.set(node, taskParam.getValue());
				continue;
			}
			
			taskParam.setValue(yamlConfig.getDouble(node));
		}
	}
	
	private static void LoadBossParams(YamlConfiguration yamlConfig) {
		for (BossParam bossParam : BossParam.values()) {
			String node = bossParam.getNode();
			
			if (!yamlConfig.contains(node)) {
				Likeaboss.logger.warning("[Likeaboss] Adding '" + node + "' in config file.");
				yamlConfig.set(node, bossParam.getValue());
				continue;
			}
			
			bossParam.setValue(yamlConfig.getBoolean(node));
		}
	}
}

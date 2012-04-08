package cam.config;

public abstract class GlobalConfig {
	
	public enum CommandParam {
		
		IGNORE_DELAY (120) {public String getNode() {return "Command.Ignore.Delay";}},
		SPAWN_MAX (50) {public String getNode() {return "Command.Spawn.Max";}};
		
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
		
		PLAYER_FOUND_BOSS_1 ("&cOh noes, that's a boss!") {public String getNode() {return "Message.PlayerFoundBoss.ToPlayer";}},
		PLAYER_FOUND_BOSS_2 ("&c{PLAYER} found a boss!") {public String getNode() {return "Message.PlayerFoundBoss.ToOthers";}},
		BOSS_FOUND_PLAYER_1 ("&cSneaky boss.") {public String getNode() {return "Message.BossFoundPlayer.ToPlayer";}},
		BOSS_FOUND_PLAYER_2 ("&cA boss found {PLAYER}!") {public String getNode() {return "Message.BossFoundPlayer.ToOthers";}},
		TOO_FAR ("&cYou're too far away.") {public String getNode() {return "Message.TooFar";}},
		PROXIMITY ("&4You feel an evil presence...") {public String getNode() {return "Message.Proximity";}};
		
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
		
		RETRIEVE_BOSS_LIST (0.5) {public String getNode() {return "Task.RetrieveBossList";}},
		BOSS_VISUAL_EFFECT (1.0) {public String getNode() {return "Task.BossVisualEffect";}},
		CHECK_BOSS_PROXIMITY (0.5) {public String getNode() {return "Task.CheckBossProximity";}},
		CHECK_ENTITY_HEALTH (2.0) {public String getNode() {return "Task.CheckEntityHealth";}},
		CHECK_ENTITY_EXISTENCE (5.0) {public String getNode() {return "Task.CheckEntityExistence";}},
		SAVE_PLAYER_DATA (600.0) {public String getNode() {return "Task.SavePlayerData";}};
		
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
		
		OVERWRITE_DROPS (false) {public String getNode() {return "Boss.OverwriteDrops";}},
		ATTACK_IMMUNE (false) {public String getNode() {return "Boss.Immunity.Attack";}},
		PROJECTILE_IMMUNE (false) {public String getNode() {return "Boss.Immunity.Projectile";}},
		BLOCK_EXPLOSION_IMMUNE (false) {public String getNode() {return "Boss.Immunity.BlockExplosion";}},
		ENTITY_EXPLOSION_IMMUNE (false) {public String getNode() {return "Boss.Immunity.EntityExplosion";}},
		FIRE_IMMUNE (true) {public String getNode() {return "Boss.Immunity.Fire";}},
		LAVA_IMMUNE (true) {public String getNode() {return "Boss.Immunity.Lava";}},
		ENCHANT_FIRETICK_IMMUNE (false) {public String getNode() {return "Boss.Immunity.EnchantFireTick";}},
		ENVIRONMENTAL_FIRETICK_IMMUNE (true) {public String getNode() {return "Boss.Immunity.EnvironmentalFireTick";}},
		FALL_IMMUNE (true) {public String getNode() {return "Boss.Immunity.Fall";}},
		CONTACT_IMMUNE (true) {public String getNode() {return "Boss.Immunity.Contact";}},
		DROWNING_IMMUNE (true) {public String getNode() {return "Boss.Immunity.Drowning";}},
		LIGHTNING_IMMUNE (false) {public String getNode() {return "Boss.Immunity.Lightning";}},
		SUFFOCATION_IMMUNE (true) {public String getNode() {return "Boss.Immunity.Suffocation";}},
		MAGIC_IMMUNE (false) {public String getNode() {return "Boss.Immunity.Magic";}},
		POISON_IMMUNE (false) {public String getNode() {return "Boss.Immunity.Poison";}};
		
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
}

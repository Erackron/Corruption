package cam.config;

public abstract class GlobalConfig {

	public enum CommandData {
		
		IGNORE_DELAY (120) {public String getNode() {return "Command.Ignore.Delay";}};
		
		private int value = 0;
		
		private CommandData(int value) {
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
	
	public enum MessageData {
		
		PLAYER_FOUND_BOSS_1 ("&cOh noes, that's a boss!") {public String getNode() {return "Message.PlayerFoundBoss.ToPlayer";}},
		PLAYER_FOUND_BOSS_2 ("&c{PLAYER} found a boss!") {public String getNode() {return "Message.PlayerFoundBoss.ToOthers";}},
		BOSS_FOUND_PLAYER_1 ("&cSneaky boss.") {public String getNode() {return "Message.BossFoundPlayer.ToPlayer";}},
		BOSS_FOUND_PLAYER_2 ("&cA boss found {PLAYER}!") {public String getNode() {return "Message.BossFoundPlayer.ToOthers";}},
		TOO_FAR ("&cYou're too far away.") {public String getNode() {return "Message.TooFar";}},
		PROXIMITY ("&4You feel an evil presence...") {public String getNode() {return "Message.Proximity";}};
		
		private String message = null;
		
		private MessageData(String message) {
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
	
	public enum TaskData {
		
		BOSS_VISUAL_EFFECT (1.0) {public String getNode() {return "Task.BossVisualEffect";}},
		CHECK_BOSS_PROXIMITY (0.5) {public String getNode() {return "Task.CheckBossProximity";}},
		CHECK_ENTITY_HEALTH (2.0) {public String getNode() {return "Task.CheckEntityHealth";}},
		CHECK_ENTITY_EXISTENCE (5.0) {public String getNode() {return "Task.CheckEntityExistence";}};
		
		private double value = 0;
		
		private TaskData(double value) {
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
}

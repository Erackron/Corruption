package cam.config;

public enum MessageData {
	
	PLAYERFOUNDBOSS1 ("Message.PlayerFoundBoss.ToPlayer", "&cOh noes, that's a boss!"),
	PLAYERFOUNDBOSS2 ("Message.PlayerFoundBoss.ToOthers", "&c{PLAYER} found a boss&c!"),
	BOSSFOUNDPLAYER1 ("Message.BossFoundPlayer.ToPlayer", "&cSneaky boss."),
	BOSSFOUNDPLAYER2 ("Message.BossFoundPlayer.ToOthers", "&cA boss found {PLAYER}!"),
	TOOFAR ("Message.TooFar", "&cYou're too far away."),
	PROXIMITY ("Message.Proximity", "&4You feel an evil presence...");
	
	private String line = null;
	private String message = null;
	
	private MessageData(String line, String message) {
		this.line = line;
		this.message = message;
	}
	
	public String getLine() {
		return line;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}

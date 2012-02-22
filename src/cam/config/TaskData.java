package cam.config;

public enum TaskData {
	
	BOSS_VISUAL_EFFECT ("Task.VisualEffect", 1.0),
	CHECK_BOSS_PROXIMITY ("Task.CheckBossProximity", 0.5),
	CHECK_ENTITY_HEALTH ("Task.CheckEntityHealth", 2.0),
	CHECK_ENTITY_EXISTENCE ("Task.CheckEntityExistence", 5.0);
	
	private String line = null;
	private double value = 0;
	
	private TaskData(String line, double value) {
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

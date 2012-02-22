package cam.config;

import java.util.ArrayList;
import java.util.List;

import cam.drop.Roll;

public class WorldDropData {
	
	private List<Roll> rolls = new ArrayList< Roll>();
	
	public WorldDropData() {
		
	}

	public void AddRoll(Roll roll) {
		rolls.add(roll);
	}
	
	public List<Roll> getRolls() {
		return rolls;
	}
}

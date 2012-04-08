package cam.drop;

import java.util.ArrayList;
import java.util.List;

public class Roll {
	
	private List<Drop> drops = new ArrayList<Drop>();
	
	public void AddDrop(Drop drop) {
		drops.add(drop);
	}
	
	public List<Drop> getDrops() {
		return drops;
	}
}

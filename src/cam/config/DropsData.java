package cam.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


public enum DropsData {
	
	FIRSTROLL_FIRSTITEM ("Drops.FirstRoll.FirstItem", 0, 264, 10, 1, 2),
	FIRSTROLL_SECONDITEM ("Drops.FirstRoll.SecondItem", 0, 266, 15, 1, 2),
	FIRSTROLL_THIRDITEM ("Drops.FirstRoll.ThirdItem", 0, 265, 20, 1, 3),
	FIRSTROLL_FOURTHITEM ("Drops.FirstRoll.FourthItem", 0, 263, 25, 1, 3),
	
	SECONDROLL_FIRSTITEM ("Drops.SecondRoll.FirstItem", 1, 364, 20, 1, 2),
	SECONDROLL_SECONDITEM ("Drops.SecondRoll.SecondItem", 1, 320, 20, 1, 2),
	SECONDROLL_THIRDITEM ("Drops.SecondRoll.ThirdItem", 1, 366, 20, 1, 2),
	SECONDROLL_FOURTHITEM ("Drops.SecondRoll.FourthItem", 1, 354, 1, 1, 1),
	
	THIRDROLL_FIRSTITEM ("Drops.ThirdRoll.FirstItem", 2, 374, 3, 1, 2),
	THIRDROLL_SECONDITEM ("Drops.ThirdRoll.SecondItem", 2, 280, 3, 1, 3),
	THIRDROLL_THIRDITEM ("Drops.ThirdRoll.ThirdItem", 2, 281, 3, 1, 1),
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

package cam.drop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import cam.Likeaboss;
import cam.Utility;
import cam.boss.Boss;
import cam.config.LabConfig;
import cam.config.WorldDropData;

public class DropCalculator {

	private LabConfig labConfig = null;
	private Map<Material, Integer> droped = new HashMap<Material, Integer>();
	
	public DropCalculator(Likeaboss plugin) {
		this.labConfig = plugin.getLabConfig();
	}
	
	public void Process(List<ItemStack> drops, Boss boss, World world) {
		if (!(drops instanceof ArrayList))
			return;
		
		WorldDropData worldDropData = labConfig.getWorldDropData(world);
		List<Roll> rolls = new ArrayList<Roll>();
		rolls.addAll(worldDropData.getRolls());
		rolls.addAll(boss.getBossData().getRolls());
		
		for (Roll roll : rolls) {
			double random = Math.random() * 100;
			double chance = 0;
			
			for (Drop drop : roll.getDrops()) {
				chance += drop.getChance();
				
				if (chance > random) {
					int quantity = Utility.Random(drop.getMinQuantity(), drop.getMaxQuantity());
					
					if (quantity == 0) //Don't drop a "ghost" item
						return;
					
					Material material = drop.getMaterial();
					ItemStack itemStack = new ItemStack(material, quantity, drop.getData());
					
					drops.add(itemStack);
					AddToDroped(material, quantity);
					break;
				}
			}
		}
	}
		
	public void AddToDroped(Material material, int quantity) {
		int value = 0;
		if (droped.containsKey(material))
			value = droped.get(material);
		droped.put(material, value + quantity);
	}
	
	public void Clear() {
		droped.clear();
	}

	public Map<Material, Integer> getDroped() {
		return droped;
	}
}

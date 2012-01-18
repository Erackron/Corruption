package cam.boss;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import cam.Utility;

public class DropManager {

	private Map<Integer, Roll> rolls = new HashMap<Integer, Roll>();
	private Map<Material, Integer> droped = new HashMap<Material, Integer>();
	
	public DropManager() {
	}
	
	public void AddPossibleDrop(int rollId, double[] values) {
		Drop drop = new Drop(rollId, values);
		
		if (rolls.containsKey(rollId))
			rolls.get(rollId).AddDrop(drop);
		else {
			Roll roll = new Roll();
			roll.AddDrop(drop);
			rolls.put(rollId, roll);
		}
	}

	public void Drop(World world, Location location) {
		
		for (Roll roll : rolls.values()) {
			double random = Math.random() * 100;
			double chance = 0;
			
			for (Drop drop : roll.getDrops()) {
				chance += drop.getChance();
				
				if (chance > random) {
					int minQuantity = drop.getMinQuantity();
					int maxQuantity = drop.getMaxQuantity();
					
					int quantity = Utility.Random(minQuantity, maxQuantity);
					
					if (quantity == 0) //Don't drop a ghost item
						return;
					
					Material material = Material.getMaterial(drop.getMaterialId());
					ItemStack itemStack = new ItemStack(material, quantity);
					
					world.dropItemNaturally(location, itemStack);
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
	
	public Map<Integer, Roll> getRolls() {
		return rolls;
	}

	public Map<Material, Integer> getDroped() {
		return droped;
	}
	
	private class Roll {

		private Set<Drop> drops = new HashSet<Drop>();
		
		public Set<Drop> getDrops() {
			return drops;
		}

		public void AddDrop(Drop drop) {
			drops.add(drop);
		}
	}

	private class Drop {
		
		private int materialId = 0;
		private int chance = 0;
		private int minQuantity = 0;
		private int maxQuantity = 0;
		
		public Drop(int materialId, double[] values) {
			this.materialId = (int) values[0];
			this.chance = (int) values[1];
			this.minQuantity = (int) values[2];
			this.maxQuantity = (int) values[3];
		}
		
		public int getMaterialId() {
			return materialId;
		}
		
		public int getChance() {
			return chance;
		}
		
		public int getMinQuantity() {
			return minQuantity;
		}
		
		public int getMaxQuantity() {
			return maxQuantity;
		}
	}
}

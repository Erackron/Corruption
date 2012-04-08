package cam.drop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cam.Likeaboss;
import cam.Utility;
import cam.boss.BossData;
import cam.config.WorldConfig;

public class DropCalculator {
	
	public List<ItemStack> CreateDrops(BossData bossData, WorldConfig worldConfig) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		List<Roll> rolls = new ArrayList<Roll>();
		rolls.addAll(worldConfig.getRolls());
		rolls.addAll(bossData.getRolls());
		
		for (Roll roll : rolls) {
			double random = Math.random() * 100;
			double chance = 0;
			
			for (Drop drop : roll.getDrops()) {
				chance += drop.getChance();
				
				if (chance > random) {
					int quantity = Utility.Random(drop.getMinQuantity(), drop.getMaxQuantity());
					
					if (quantity == 0) //Don't drop a "ghost" item
						break;
					
					Material material = drop.getMaterial();
					ItemStack itemStack = new ItemStack(material, quantity, drop.getData());
					
					drops.add(itemStack);
					Likeaboss.instance.getStats().AddDrops(material, quantity);
					break;
				}
			}
		}
		
		return drops;
	}
}

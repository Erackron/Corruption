<<<<<<< HEAD:src/com/mcdr/likeaboss/drop/DropCalculator.java
package com.mcdr.likeaboss.drop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mcdr.likeaboss.Utility;
import com.mcdr.likeaboss.entity.BossData;
import com.mcdr.likeaboss.stats.StatsManager;
import com.mcdr.likeaboss.world.WorldData;


public abstract class DropCalculator {
	public static List<ItemStack> CreateDrops(BossData bossData, WorldData worldData) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		List<Roll> rolls = new ArrayList<Roll>();
		rolls.addAll(worldData.getRolls());
		rolls.addAll(bossData.getRolls());
		
		for (Roll roll : rolls) {
			double random = Utility.random.nextInt(100);
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
					StatsManager.AddDrops(material, quantity);
					break;
				}
			}
		}
		
		return drops;
	}
}
=======
package com.mcdr.likeaboss.drop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mcdr.likeaboss.Utility;
import com.mcdr.likeaboss.entity.BossData;
import com.mcdr.likeaboss.stats.StatsManager;
import com.mcdr.likeaboss.world.WorldData;


public abstract class DropCalculator {
	public static List<ItemStack> CreateDrops(BossData bossData, WorldData worldData) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		List<Roll> rolls = new ArrayList<Roll>();
		rolls.addAll(worldData.getRolls());
		rolls.addAll(bossData.getRolls());
		
		for (Roll roll : rolls) {
			double random = Utility.random.nextInt(100);
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
					StatsManager.AddDrops(material, quantity);
					break;
				}
			}
		}
		
		return drops;
	}
}
>>>>>>> origin/EquipmentExpansion:src/com/mcdr/likeaboss/drop/DropCalculator.java

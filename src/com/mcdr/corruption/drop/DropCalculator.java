package com.mcdr.corruption.drop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.stats.StatsManager;
import com.mcdr.corruption.util.Utility;
import com.mcdr.corruption.world.WorldData;


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
					
					ItemStack itemStack = drop.getItem().getItemStack();

                    itemStack.setAmount(quantity);

					drops.add(itemStack);
					StatsManager.AddDrops(itemStack.getType(), quantity);
					break;
				}
			}
		}
		
		return drops;
	}
}

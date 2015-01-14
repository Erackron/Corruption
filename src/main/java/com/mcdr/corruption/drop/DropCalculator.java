package com.mcdr.corruption.drop;

import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.stats.StatsManager;
import com.mcdr.corruption.util.MathUtil;
import com.mcdr.corruption.world.WorldData;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public abstract class DropCalculator {
    public static List<ItemStack> CreateDrops(BossData bossData, WorldData worldData) {
        List<ItemStack> drops = new ArrayList<ItemStack>();
        List<Roll> rolls = new ArrayList<Roll>();
        rolls.addAll(worldData.getRolls());
        rolls.addAll(bossData.getRolls());

        for (Roll roll : rolls) {
            double random = MathUtil.random.nextInt(100);
            double chance = 0;

            for (Drop drop : roll.getDrops()) {
                chance += drop.getChance();

                if (chance > random) {
                    int quantity = MathUtil.Random(drop.getMinQuantity(), drop.getMaxQuantity());

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

package com.mcdr.corruption.ability;

import com.mcdr.corruption.config.BossConfig;
import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.util.Utility;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.*;

import java.util.*;

public class Summon extends Ability {

    private int minAmount = 5;
    private int maxAmount = 10;
    private int minDistance = 0;
    private int maxDistance = 10;
    private boolean strikeLightning = false;
    private boolean fireResistant = false;

    private Map<SummonType, Integer> allowedBosses = new HashMap<SummonType, Integer>();
    private int totalChance;

    @Override
    public Ability clone() {
        Summon summon = new Summon();
        summon.setMinAmount(minAmount);
        summon.setMaxAmount(maxAmount);
        summon.setMinDistance(minDistance);
        summon.setMaxDistance(maxDistance);
        summon.setAllowedBosses(allowedBosses);
        summon.setLightning(strikeLightning);
        summon.setFireResistant(fireResistant);
        copySettings(summon);
        return summon;
    }

    public boolean Execute(LivingEntity livingEntity, Boss boss) {
        if (!super.Execute(livingEntity, boss))
            return false;

        if (!summon(boss.getLivingEntity().getLocation()))
            return false;

        useCooldown(boss);
        sendAreaMessage(boss, livingEntity);
        return true;
    }

    public boolean Execute(LivingEntity livingEntity, Location lastLoc, Boss boss) {
        if (!super.Execute(livingEntity, lastLoc, boss))
            return false;

        if (!summon(lastLoc))
            return false;

        useCooldown(boss);
        sendAreaMessage(lastLoc, boss.getName(), livingEntity);
        return true;
    }

    private boolean summon(Location location) {
        int amount = Utility.random.nextInt((maxAmount - minAmount) + 1) + minAmount;
        List<Block> validBlocks = findValidBlocks(location, minDistance, maxDistance);

        if (validBlocks.isEmpty() || amount <= 0)
            return false;

        for (Map.Entry<SummonType, Integer> entry : allowedBosses.entrySet()) {
            ArrayList<Entity> spawnedEntities = new ArrayList<Entity>();
            long amountPerType = Math.round((double) entry.getValue() / totalChance * amount);

            for (int i = 1; i <= amountPerType; i++) {
                Block block = validBlocks.get(Utility.random.nextInt(validBlocks.size()));
                Entity entity = location.getWorld().spawnEntity(block.getLocation(), entry.getKey().getMonsterType());
                spawnedEntities.add(entity);
                if (strikeLightning)
                    location.getWorld().strikeLightningEffect(block.getLocation());
            }

            int bossAmount = (int) Math.round((entry.getKey().getBossChance() / 100.0) * amountPerType);

            int finger = 0;
            for (Map.Entry<BossData, Integer> bossEntry : entry.getKey().getBossesAmounts(bossAmount).entrySet()) {
                for (int i = 0; i < bossEntry.getValue(); i++) {
                    CorEntityManager.adjustSpecificEntities((LivingEntity) spawnedEntities.get(finger + i), bossEntry.getKey(), entry.getKey().getMonsterType());
                    Boss boss = new Boss((LivingEntity) spawnedEntities.get(finger + i), bossEntry.getKey());
                    CorEntityManager.addBoss(boss);
                }

                finger += bossEntry.getValue();
            }
        }
        return true;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public void setMinDistance(int minDistance) {
        this.minDistance = minDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public void setAllowedBosses(Map<SummonType, Integer> allowedBosses) {
        this.allowedBosses = allowedBosses;
        int total = 0;
        for (Integer i : allowedBosses.values()) {
            total += i;
        }
        this.totalChance = total;
    }

    public void setLightning(boolean lightning) {
        this.strikeLightning = lightning;
    }

    public void setFireResistant(boolean fireResistant) {
        this.fireResistant = fireResistant;
    }
}

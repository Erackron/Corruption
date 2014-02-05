package com.mcdr.corruption.ability;

import com.mcdr.corruption.config.BossConfig;
import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.util.CorLogger;
import com.mcdr.corruption.util.Utility;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Summon extends Ability {

    private EntityType monsterType;
    private int minAmount;
    private int maxAmount;
    private int minDistance;
    private int maxDistance;
    private int bossChance;

    private boolean canBeBoss;
    private boolean alwaysBoss;

    private List<String> allowedBosses;

    @Override
    public Ability clone() {
        Summon summon = new Summon();
        summon.setMonsterType(monsterType);
        summon.setMinAmount(minAmount);
        summon.setMaxAmount(maxAmount);
        summon.setMinDistance(minDistance);
        summon.setMaxDistance(maxDistance);
        summon.setBossChance(bossChance);
        summon.setCanBeBoss(canBeBoss);
        summon.setAlwaysBoss(alwaysBoss);
        summon.setAllowedBosses(allowedBosses);
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

    public boolean Execute(LivingEntity livingEntity, Location lastLoc, Boss boss){
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

        ArrayList<Entity> spawnedEntities = new ArrayList<Entity>();
        for (int i = 1; i <= amount; i++) {
            Block block = validBlocks.get(Utility.random.nextInt(validBlocks.size()));
            spawnedEntities.add(location.getWorld().spawnEntity(block.getLocation(), monsterType));
        }

        if (canBeBoss || alwaysBoss) {
            int bossAmount;
            bossAmount = (int) Math.round((bossChance / 100.0) * amount);
            if (alwaysBoss)
                bossAmount = amount;

            Collections.shuffle(spawnedEntities);

            for (int i = 0; i < bossAmount; i++) {
                String bossName = allowedBosses.get(Utility.random.nextInt(allowedBosses.size()));
                BossData bossData = BossConfig.getBossesData().get(bossName);
                if (bossData == null || !bossData.getEntityType().equals(monsterType))
                    continue;

                CorEntityManager.adjustSpecificEntities((LivingEntity) spawnedEntities.get(i), bossData, monsterType);
                Boss boss = new Boss((LivingEntity) spawnedEntities.get(i), bossData);
                CorEntityManager.addBoss(boss);
            }
        }
        return true;
    }

    public void setMonsterType(EntityType entityType) {
        this.monsterType = entityType;
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

    public void setBossChance(int bossChance) {
        this.bossChance = bossChance;
    }

    public void setCanBeBoss(boolean canBeBoss) {
        this.canBeBoss = canBeBoss;
    }

    public void setAlwaysBoss(boolean alwaysBoss) {
        this.alwaysBoss = alwaysBoss;
    }

    public void setAllowedBosses(List<String> allowedBosses) {
        this.allowedBosses = allowedBosses;
    }
}

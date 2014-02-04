package com.mcdr.corruption.ability;

import com.mcdr.corruption.CorruptionAPI;
import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.util.Utility;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.List;

public class Summon extends Ability {

    private EntityType monsterType;
    private int minAmount;
    private int maxAmount;
    private int minDistance;
    private int maxDistance;
    private int corruptedChance;

    private boolean canBeCorrupted;
    private boolean alwaysCorrupted;

    @Override
    public Ability clone() {
        Summon summon = new Summon();
        summon.setMonsterType(monsterType);
        summon.setMinAmount(minAmount);
        summon.setMaxAmount(maxAmount);
        summon.setMinDistance(minDistance);
        summon.setMaxDistance(maxDistance);
        summon.setCorruptedChance(corruptedChance);
        summon.setCanBeCorrupted(canBeCorrupted);
        summon.setAlwaysCorrupted(alwaysCorrupted);
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

    private boolean summon(Location location) {
        int amount = Utility.random.nextInt((maxAmount - minAmount) + 1) + minAmount;
        List<Block> validBlocks = findValidBlocks(location, minDistance, maxDistance);

        if (validBlocks.isEmpty())
            return false;

        for (int i = 1; i <= amount; i++) {
            Block block = validBlocks.get(Utility.random.nextInt(validBlocks.size()));
            location.getWorld().spawnEntity(block.getLocation(), monsterType);
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

    public void setCorruptedChance(int corruptedChance) {
        this.corruptedChance = corruptedChance;
    }

    public void setCanBeCorrupted(boolean canBeCorrupted) {
        this.canBeCorrupted = canBeCorrupted;
    }

    public void setAlwaysCorrupted(boolean alwaysCorrupted) {
        this.alwaysCorrupted = alwaysCorrupted;
    }
}

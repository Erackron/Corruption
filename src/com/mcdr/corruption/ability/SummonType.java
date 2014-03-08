package com.mcdr.corruption.ability;

import com.mcdr.corruption.config.BossConfig;
import com.mcdr.corruption.entity.data.BossData;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummonType {

    private EntityType monsterType;
    private double bossChance = 50;

    private Map<String, Integer> allowedBosses;
    private int totalBossChance;

    public SummonType(EntityType monsterType, double bossChance, List<String> allowedBosses) {
        this.monsterType = monsterType;
        this.bossChance = bossChance;
        parseAllowedBosses(allowedBosses);
    }

    public double getBossChance() {
        return bossChance;
    }

    public void setBossChance(double bossChance) {
        this.bossChance = bossChance;
    }

    public Map<String, Integer> getAllowedBosses() {
        return allowedBosses;
    }

    public void setAllowedBosses(Map<String, Integer> allowedBosses) {
        this.allowedBosses = allowedBosses;
    }

    public void parseAllowedBosses(List<String> allowedBosses) {
        this.allowedBosses = new HashMap<String, Integer>();
        for (String boss : allowedBosses) {
            String[] params = boss.split(" ");

            if (params.length < 2)
                continue;

            int value;

            try {
                value = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                value = 1;
            }

            this.allowedBosses.put(params[0], value);
        }

        int total = 0;
        for (Integer i : this.allowedBosses.values()) {
            total += i;
        }
        totalBossChance = total;
    }

    public EntityType getMonsterType() {
        return monsterType;
    }

    public void setMonsterType(EntityType monsterType) {
        this.monsterType = monsterType;
    }

    public Map<BossData, Integer> getBossesAmounts(int totalAmount) {
        Map<BossData, Integer> bossesAmounts = new HashMap<BossData, Integer>();
        for (Map.Entry<String, Integer> entry : allowedBosses.entrySet()) {
            int amountPerBoss = (int) Math.round((double) entry.getValue() / totalBossChance * totalAmount);
            BossData bossData = BossConfig.getBossesData().get(entry.getKey());
            if (bossData == null || !bossData.getEntityType().equals(monsterType))
                continue;
            bossesAmounts.put(bossData, amountPerBoss);
        }

        return bossesAmounts;
    }
}

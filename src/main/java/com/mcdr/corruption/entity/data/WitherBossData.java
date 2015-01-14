package com.mcdr.corruption.entity.data;

import org.bukkit.entity.EntityType;

public class WitherBossData extends BossData {
    private int regenPerSecond;

    public WitherBossData(String name, EntityType entityType, int regenPerSecond) {
        super(name, entityType);
        this.regenPerSecond = regenPerSecond;
    }

    public int getRegenPerSecond() {
        return regenPerSecond;
    }

    public void setRegenPerSecond(int regenPerSecond) {
        this.regenPerSecond = regenPerSecond;
    }

}

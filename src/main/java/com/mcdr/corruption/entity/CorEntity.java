package com.mcdr.corruption.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;

public abstract class CorEntity {
    protected LivingEntity livingEntity;

    public abstract void OnDeath(EntityDeathEvent event);

    public boolean IsEntityAlive() {
        return livingEntity.isValid();
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }

    public void setLivingEntity(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }
}

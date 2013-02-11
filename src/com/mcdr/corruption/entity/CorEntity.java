package com.mcdr.corruption.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;

public abstract class CorEntity {
	protected LivingEntity livingEntity;
	private int previousTicksLived;
	
	public abstract void OnDeath(EntityDeathEvent event);
	
	//Sometimes Entity.isDead() isn't enough, most certainly a MC/CraftBukkit bug.
	//FIXME This method seems to make Corruption lose track of bosses if used,
	//it works in the CheckEntityExistance task, but if (also) used elsewhere, it causes trouble
	public boolean IsEntityAlive() {
		int currentTicksLived = livingEntity.getTicksLived();
		
		if (currentTicksLived == previousTicksLived)
			return false;
		
		previousTicksLived = currentTicksLived;
		return true;
	}
	
	public LivingEntity getLivingEntity() {
		return livingEntity;
	}
	
	public void setLivingEntity(LivingEntity livingEntity) {
		this.livingEntity = livingEntity;
	}
}

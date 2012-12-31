<<<<<<< HEAD:src/com/mcdr/likeaboss/entity/LabEntity.java
package com.mcdr.likeaboss.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;

public abstract class LabEntity {
	protected LivingEntity livingEntity;
	private int previousTicksLived;
	
	public abstract void OnDeath(EntityDeathEvent event);
	
	//Sometimes Entity.isDead() isn't enough, most certainly a MC/CraftBukkit bug.
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
=======
package com.mcdr.likeaboss.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;

public abstract class LabEntity {
	protected LivingEntity livingEntity;
	private int previousTicksLived;
	
	public abstract void OnDeath(EntityDeathEvent event);
	
	//Sometimes Entity.isDead() isn't enough, most certainly a MC/CraftBukkit bug.
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
>>>>>>> origin/EquipmentExpansion:src/com/mcdr/likeaboss/entity/LabEntity.java

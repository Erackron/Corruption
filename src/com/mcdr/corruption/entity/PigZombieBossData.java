package com.mcdr.corruption.entity;

import org.bukkit.entity.EntityType;

public class PigZombieBossData extends ZombieBossData{
	private boolean isAngry;
	
	public PigZombieBossData(String name, EntityType entityType, boolean isBaby, boolean isAngry) {
		super(name, entityType, isBaby, false);
		this.isAngry = isAngry;
	}

	public boolean isAngry() {
		return isAngry;
	}

	public void setAngry(boolean isAngry) {
		this.isAngry = isAngry;
	}
}

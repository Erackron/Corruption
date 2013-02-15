package com.mcdr.corruption.entity.data;

import org.bukkit.entity.EntityType;

public class ZombieBossData extends BossData {
	private boolean isBaby;
	private boolean isVillager;

	public ZombieBossData(String name, EntityType entityType, boolean isBaby, boolean isVillager) {
		super(name, entityType);
		this.isBaby = isBaby;
		this.isVillager = isVillager;
	}
	
	public boolean isBaby() {
		return isBaby;
	}

	public void setBaby(boolean isBaby) {
		this.isBaby = isBaby;
	}

	public boolean isVillager() {
		return isVillager;
	}

	public void setVillager(boolean isVillager) {
		this.isVillager = isVillager;
	}
}

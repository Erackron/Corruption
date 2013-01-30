package com.mcdr.likeaboss.entity;

import org.bukkit.entity.EntityType;

public class SlimeBossData extends BossData{
	
	private int size;

	public SlimeBossData(String name, EntityType entityType, int size) {
		super(name, entityType);
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size){
		this.size = size;
	}
}

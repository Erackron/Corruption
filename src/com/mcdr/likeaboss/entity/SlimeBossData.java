package com.mcdr.likeaboss.entity;

import org.bukkit.entity.EntityType;

public class SlimeBossData extends BossData{
	
	private int minsize;
	private int maxsize;

	public SlimeBossData(String name, EntityType entityType, int minsize, int maxsize) {
		super(name, entityType);
		this.minsize = minsize;
		this.maxsize = maxsize;
	}
	
	public int getMinimalSize() {
		return minsize;
	}
	
	public int getMaximalSize(){
		return maxsize;
	}
	
	public void setMinimalSize(int minsize){
		this.minsize = minsize;
	}
	
	public void setMaximalSize(int maxsize){
		this.maxsize = maxsize;
	}
}

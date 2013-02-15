package com.mcdr.corruption.entity.data;

import org.bukkit.entity.EntityType;

public class SlimeBossData extends BossData{
	
	private int minsize;
	private int maxsize;

	public SlimeBossData(String name, EntityType entityType, int minsize, int maxsize) {
		super(name, entityType);
		this.minsize = minsize;
		this.maxsize = maxsize;
	}
	
	public int getMinimumSize() {
		return minsize;
	}
	
	public int getMaximumSize(){
		return maxsize;
	}
	
	public void setMinimumSize(int minsize){
		this.minsize = minsize;
	}
	
	public void setMaximumSize(int maxsize){
		this.maxsize = maxsize;
	}
}

package com.mcdr.corruption.entity;

import org.bukkit.entity.EntityType;

public class GhastBossData extends BossData {
	
	private boolean returnToSenderImmune;

	public GhastBossData(String name, EntityType entityType, boolean returnToSenderImmune) {
		super(name, entityType);
		this.returnToSenderImmune = returnToSenderImmune;
	}
	
	public boolean isReturnToSenderImmune(){
		return returnToSenderImmune;
	}
	
	public void setReturnToSenderImmune(boolean returnToSenderImmune){
		this.returnToSenderImmune = returnToSenderImmune;
	}

}

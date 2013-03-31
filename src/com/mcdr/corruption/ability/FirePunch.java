package com.mcdr.corruption.ability;

import org.bukkit.entity.LivingEntity;

import com.mcdr.corruption.entity.Boss;


public class FirePunch extends Ability {
	private int ticks = 2;
	
	public FirePunch clone(){
		FirePunch fp = new FirePunch();
		copySettings(fp);
		fp.setTicks(this.ticks);
		return fp;
	}
	
	/**
     * Normal Execute
     */
	public void Execute(LivingEntity livingEntity, Boss boss) {
		super.Execute(livingEntity, boss);
		int fireTicks = livingEntity.getFireTicks();
			
		//Somehow getFireTicks returns -20 when not on fire
		if (fireTicks < 0)
			livingEntity.setFireTicks(ticks * 20);
		else
			livingEntity.setFireTicks(fireTicks + ticks * 20);
			
		useCooldown(boss);
		sendMessage(boss, livingEntity);
		
	}
	
	public void setTicks(int ticks) {
		//+1 because the first tick doesn't do any damage
		this.ticks = ticks + 1;
	}
}

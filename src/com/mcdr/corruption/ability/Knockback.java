package com.mcdr.corruption.ability;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.entity.Boss;


public class Knockback extends Ability {
	private double horizontalCoef = 2;
	private double verticalCoef = 3;
	
	public Knockback clone(){
		Knockback kb = new Knockback();
		copySettings(kb);
		kb.setHorizontalCoef(this.horizontalCoef);
		kb.setVerticalCoef(this.verticalCoef);
		return kb;
	}
	
	/**
	 * OnDeath Execute
	 */
	public void Execute(LivingEntity livingEntity, Location lastLoc, Boss boss){
		super.Execute(livingEntity, lastLoc, boss);
		Corruption.scheduler.scheduleSyncDelayedTask(Corruption.in, new VelocityMultiplier(livingEntity));
		sendMessage(boss.getName(), livingEntity);
	}
	
	/**
	 * Normal Execute
	 */
	public void Execute(LivingEntity livingEntity, Boss boss) {
		super.Execute(livingEntity, boss);
		Corruption.scheduler.scheduleSyncDelayedTask(Corruption.in, new VelocityMultiplier(livingEntity));
		useCooldown(boss);
		sendMessage(boss, livingEntity);
	}
	
	public void setHorizontalCoef(double horizontalCoef) {
		this.horizontalCoef = horizontalCoef;
	}
	
	public void setVerticalCoef(double verticalCoef) {
		this.verticalCoef = verticalCoef;
	}
	
	private class VelocityMultiplier implements Runnable {
		
		private LivingEntity livingEntity;
		
		public VelocityMultiplier(LivingEntity livingEntity) {
			this.livingEntity = livingEntity;
		}
		
		@Override
		public void run() {
			livingEntity.setVelocity(livingEntity.getVelocity().multiply(new Vector(horizontalCoef, verticalCoef, horizontalCoef)));
		}
	}
}

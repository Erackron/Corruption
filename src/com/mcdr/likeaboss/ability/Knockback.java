package com.mcdr.likeaboss.ability;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import com.mcdr.likeaboss.Likeaboss;
import com.mcdr.likeaboss.entity.Boss;


public class Knockback extends Ability {
	private double horizontalCoef = 2;
	private double verticalCoef = 3;
	
	public Knockback() {
		activationConditions.add(ActivationCondition.ONATTACK);
	}
	
	@Override
	public void Execute(EntityDamageEvent event, LivingEntity livingEntity, Boss boss) {
		if(checkChance()){
			Likeaboss.scheduler.scheduleSyncDelayedTask(Likeaboss.in, new VelocityMultiplier(livingEntity));
			useCooldown(boss);
			sendMessage(boss, livingEntity);
		}
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

package com.mcdr.likeaboss.ability;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import com.mcdr.likeaboss.entity.Boss;


public class FirePunch extends Ability {
	private int ticks = 2;
	
	public FirePunch() {
		activationConditions.add(ActivationCondition.ONATTACK);
	}
	
	@Override
	public void Execute(EntityDamageEvent event, LivingEntity livingEntity, Boss boss) {
		if(checkChance()){
			int fireTicks = livingEntity.getFireTicks();
			
			//Somehow getFireTicks returns -20 when not on fire
			if (fireTicks < 0)
				livingEntity.setFireTicks(ticks * 20);
			else
				livingEntity.setFireTicks(fireTicks + ticks * 20);
			
			useCooldown(boss);
			sendMessage(boss, livingEntity);
		}
	}
	
	public void setTicks(int ticks) {
		//+1 because the first tick doesn't do any damage
		this.ticks = ticks + 1;
	}
}

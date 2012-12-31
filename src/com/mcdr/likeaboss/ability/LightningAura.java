package com.mcdr.likeaboss.ability;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import com.mcdr.likeaboss.entity.Boss;

public class LightningAura extends Ability{
	private float radius;

	public LightningAura() {
		activationConditions.add(ActivationCondition.ONATTACK);
		activationConditions.add(ActivationCondition.ONDEFENSE);
	}
	
	public void setRadius(float radius){
		this.radius = radius;
	}
	
	public void Execute(EntityDamageEvent event, LivingEntity livingEntity, Boss boss){
		if(checkChance()){
			List<Entity> entitylist = livingEntity.getNearbyEntities(radius, radius, radius);
			for(Entity e : entitylist){
				if(e.getType() == EntityType.PLAYER){
					livingEntity.getWorld().strikeLightning(e.getLocation());
				}
			}
		}
	}

}

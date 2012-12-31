<<<<<<< HEAD:src/com/mcdr/likeaboss/ability/Potion.java
package com.mcdr.likeaboss.ability;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcdr.likeaboss.entity.Boss;


public class Potion extends Ability {
	private int amplifier = 0;
	private int duration = 3;
	private String effect = "";
	private boolean targetSelf = false;
	protected double chance = 25.0;
	
	public Potion() {
	}
	
	@Override
	public void Execute(EntityDamageEvent event, LivingEntity livingEntity, Boss boss) {
		if(checkChance() && effect != ""){
			PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(effect), duration, amplifier);
			if (targetSelf) {
				boss.getLivingEntity().addPotionEffect(potionEffect, true);
				sendMessage(boss);
			} else {
				livingEntity.addPotionEffect(potionEffect, true);
				sendMessage(boss, livingEntity);
			}
			useCooldown(boss);
		}
	}
	
	public void setEffect(String effect){
		this.effect = effect;
	}
	
	public void setAmplifier(int amplifier) {
		this.amplifier = amplifier;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public void setTarget(String target){
		targetSelf = target.equalsIgnoreCase("self");
		if (targetSelf)
			activationConditions.add(ActivationCondition.ONDEFENSE);
		else
			activationConditions.add(ActivationCondition.ONATTACK);
	}
}
=======
package com.mcdr.likeaboss.ability;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcdr.likeaboss.entity.Boss;


public class Potion extends Ability {
	private int amplifier = 0;
	private int duration = 3;
	private String effect = "";
	private boolean targetSelf = false;
	protected double chance = 25.0;
	
	public Potion() {
	}
	
	@Override
	public void Execute(EntityDamageEvent event, LivingEntity livingEntity, Boss boss) {
		if(checkChance() && effect != ""){
			PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(effect), duration, amplifier);
			if (targetSelf) {
				boss.getLivingEntity().addPotionEffect(potionEffect, true);
				sendMessage(boss);
			} else {
				livingEntity.addPotionEffect(potionEffect, true);
				sendMessage(boss, livingEntity);
			}
			useCooldown(boss);
		}
	}
	
	public void setEffect(String effect){
		this.effect = effect;
	}
	
	public void setAmplifier(int amplifier) {
		this.amplifier = amplifier;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public void setTarget(String target){
		targetSelf = target.equalsIgnoreCase("self");
		if (targetSelf)
			activationConditions.add(ActivationCondition.ONDEFENSE);
		else
			activationConditions.add(ActivationCondition.ONATTACK);
	}
}
>>>>>>> origin/EquipmentExpansion:src/com/mcdr/likeaboss/ability/Potion.java

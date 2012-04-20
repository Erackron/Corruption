package cam.ability;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import cam.entity.Boss;

public class Slow extends Ability {
	private int amplifier;
	private int duration;
	
	public Slow() {
		activationConditions.add(ActivationCondition.ONATTACK);
	}
	
	@Override
	public void Execute(EntityDamageEvent event, LivingEntity livingEntity, Boss boss) {
		PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW, duration, amplifier);
		
		livingEntity.addPotionEffect(potionEffect, true);
	}
	
	public void setAmplifier(int amplifier) {
		this.amplifier = amplifier;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
}

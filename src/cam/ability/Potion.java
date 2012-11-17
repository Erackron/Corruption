package cam.ability;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import cam.entity.Boss;

public class Potion extends Ability {
	private int amplifier = 2;
	private int duration = 3;
	private String effect = "";
	private boolean targetSelf = false;
	protected double chance = 25.0;
	
	public Potion(String target) {
		targetSelf = target.equalsIgnoreCase("self");
		if (targetSelf)
			activationConditions.add(ActivationCondition.ONDEFENSE);
		else
			activationConditions.add(ActivationCondition.ONATTACK);	
	}
	
	@Override
	public void Execute(EntityDamageEvent event, LivingEntity livingEntity, Boss boss) {
		if(checkChance() && effect != ""){
			PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(effect), duration, amplifier);
			if (targetSelf) {
				boss.getLivingEntity().addPotionEffect(potionEffect, true);
			} else {
				livingEntity.addPotionEffect(potionEffect, true);
			}
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
	}
}

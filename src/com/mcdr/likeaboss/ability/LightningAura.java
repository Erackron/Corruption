package com.mcdr.likeaboss.ability;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.mcdr.likeaboss.entity.Boss;
import com.mcdr.likeaboss.player.LabPlayer;
import com.mcdr.likeaboss.player.LabPlayerManager;
import com.mcdr.likeaboss.util.Utility;

public class LightningAura extends Ability{
	
	private int radius = 5;
	private int damage = 2;
	private boolean noFire = false;
	private boolean armorPierce = false;

	public LightningAura() {
		activationConditions.add(ActivationCondition.ONATTACK);
	}
	
	public void setRadius(int radius){
		this.radius = radius;
	}
	
	public void setDamage(int damage){
		this.damage = damage - 1;
	}
	
	public void setNoFire(boolean noFire){
		this.noFire = noFire;
	}
	
	public void setArmorPierce(boolean armorPierce){
		this.armorPierce = armorPierce;
	}
	
	public void Execute(EntityDamageEvent event, LivingEntity livingEntity, Boss boss){
		if(checkChance()){			
			for (LabPlayer labPlayer : LabPlayerManager.getLabPlayers()) {
				Player player = labPlayer.getPlayer();
				World world = player.getWorld();
				
				if (Utility.isNear(player.getLocation(), boss.getLivingEntity().getLocation(), 0, radius)) {
					sendMessage(boss, livingEntity);
					world.strikeLightning(player.getLocation());
					
					if(armorPierce){
						player.damage(ArmorPierce.getNewDamage(player, damage, 100));
					} else{
						player.damage(damage);
					}
					
					if(noFire){
						player.setFireTicks(0);
					}
				}
			}
		}
	}
}

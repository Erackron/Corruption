package com.mcdr.likeaboss.ability;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.mcdr.likeaboss.entity.Boss;
import com.mcdr.likeaboss.player.LabPlayer;
import com.mcdr.likeaboss.player.LabPlayerManager;
import com.mcdr.likeaboss.utility.Utility;

public class LightningAura extends Ability{
	private int radius;
	private int damage;

	public LightningAura() {
		activationConditions.add(ActivationCondition.ONATTACK);
	}
	
	public void setRadius(int radius){
		this.radius = radius;
	}
	
	public void setDamage(int damage){
		this.damage = damage;
	}
	
	public void Execute(EntityDamageEvent event, LivingEntity livingEntity, Boss boss){
		if(checkChance()){			
			for (LabPlayer labPlayer : LabPlayerManager.getLabPlayers()) {
				Player player = labPlayer.getPlayer();
				World world = player.getWorld();
				
				if (Utility.isNear(player.getLocation(), boss.getLivingEntity().getLocation(), 0, radius)) {
					sendMessage(boss, livingEntity);
					world.strikeLightningEffect(player.getLocation());
					
					if(damage >= player.getHealth()){
						player.setHealth(0);
					}
					else{
						player.setHealth(player.getHealth() - damage);
					}				
				}
			}
		}
	}
}

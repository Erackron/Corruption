package com.mcdr.likeaboss.ability;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.mcdr.likeaboss.entity.Boss;
import com.mcdr.likeaboss.player.LabPlayer;
import com.mcdr.likeaboss.player.LabPlayerManager;
import com.mcdr.likeaboss.util.Utility;

public class LightningAura extends Ability{
	
	private int radius = 5;
	private int damage = 2;
	private boolean fire = false;
	private boolean armorPierce = false;
	protected double chance = 50.0;

	public LightningAura() {
		activationConditions.add(ActivationCondition.ONATTACK);
	}
	
	public void setRadius(int radius){
		this.radius = radius;
	}
	
	public void setDamage(int damage){
		this.damage = damage - 1;
	}
	
	public void setFire(boolean fire){
		this.fire = fire;
	}
	
	public void setArmorPierce(boolean armorPierce){
		this.armorPierce = armorPierce;
	}
	
	public void Execute(LivingEntity livingEntity, Boss boss){
		super.Execute(livingEntity, boss);
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
				
				if(!fire){
					player.setFireTicks(-20);
				}
			}
		}
		useCooldown(boss);		
	}
}

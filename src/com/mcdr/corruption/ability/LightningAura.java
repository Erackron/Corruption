package com.mcdr.corruption.ability;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.player.CorPlayer;
import com.mcdr.corruption.player.CorPlayerManager;
import com.mcdr.corruption.util.Utility;

public class LightningAura extends Ability{
	
	private int damage = 2;
	private boolean fire = false;
	private boolean armorPierce = false;
	protected double chance = 50.0;
	
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
		int radius = getMaxRange();
		for (CorPlayer corPlayer : CorPlayerManager.getCorPlayers()) {
			Player player = corPlayer.getPlayer();
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

package com.mcdr.corruption.ability;

import org.bukkit.Location;
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

    public LightningAura clone(){
		LightningAura la = new LightningAura();
		copySettings(la);
		la.setDamage(this.damage);
		la.setFire(this.fire);
		la.setArmorPierce(this.armorPierce);
		return la;
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
	
	/**
	 * OnDeath Execute
	 */
	public boolean Execute(LivingEntity livingEntity, Location lastLoc, Boss boss){
		if(!super.Execute(livingEntity, lastLoc, boss))
			return false;
		strikeLightning(lastLoc, boss.getName());
		return true;
	}
	
	/**
	 * Normal Execute
	 */
	public boolean Execute(LivingEntity livingEntity, Boss boss){
		if(!super.Execute(livingEntity, boss))
			return false;
		strikeLightning(boss.getLivingEntity().getLocation(), boss.getBossData().getName());
		useCooldown(boss);
		return true;
	}
	
	private void strikeLightning(Location centreLoc, String bossName){
		int radius = getMaxRange();
		for (CorPlayer corPlayer : CorPlayerManager.getCorPlayers()) {
			Player player = corPlayer.getPlayer();
			World world = player.getWorld();
			
			if (Utility.isNear(player.getLocation(), centreLoc, 0, radius)) {
				if(corPlayer.getCorPlayerData().getIgnore())
					continue;
				sendMessage(bossName, player);
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
	}
}

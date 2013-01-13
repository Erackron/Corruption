package com.mcdr.likeaboss.ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import com.mcdr.likeaboss.Likeaboss;
import com.mcdr.likeaboss.entity.Boss;

public class Bomb extends Ability {
	
	private int fuse = 80;
	private float radius = 3F;
	private boolean destroyWorld = true;
	private boolean fire = true;
	protected double chance = 10.0;
	
    public Bomb(){
		activationConditions.add(ActivationCondition.ONATTACK);
		activationConditions.add(ActivationCondition.ONDEFENSE);
	}
    
    public void setFuseTicks(int fuseticks){
    	this.fuse = fuseticks;
    }
    
    public void setRadius(float radius){
    	this.radius = radius;
    }
    
    public void setDestroyWorld(boolean destroyWorld){
    	this.destroyWorld = destroyWorld;
    }
    
    public void setFire(boolean fire){
    	this.fire = fire;
    }
    
    public void Execute(EntityDamageEvent event, LivingEntity livingEntity, Boss boss) {
		if(checkChance()){
	    	// Grab the target, or a random player.      
	        final World world = livingEntity.getWorld();
	        final Location loc = livingEntity.getLocation();
	        
	        Block b = null;
			try {
				b = world.getBlockAt(loc);
			} catch (NullPointerException e) {
				return;
			} 
	        
			b.setType(Material.BEDROCK);
	        
	        Likeaboss.scheduler.scheduleSyncDelayedTask(Likeaboss.in, new Runnable() {
	            public void run() {
	                world.getBlockAt(loc).breakNaturally();
	                world.createExplosion(loc.getX(), loc.getY(), loc.getZ(), radius, fire, destroyWorld);
	            }
	        }, fuse);
	        
	        useCooldown(boss);
	        sendAreaMessage(boss, livingEntity);
		}
        
    }
}
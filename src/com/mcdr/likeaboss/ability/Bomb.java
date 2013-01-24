package com.mcdr.likeaboss.ability;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import com.mcdr.likeaboss.Likeaboss;
import com.mcdr.likeaboss.entity.Boss;
import com.mcdr.likeaboss.util.Utility;

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
    
    public void Execute(LivingEntity livingEntity, Boss boss) {
		super.Execute(livingEntity, boss);
	    final List<Block> validBlocks = findValidBlocks(livingEntity.getLocation(), 0, 3);
	    
	    if (validBlocks.isEmpty())
	    	return;
	    
        Block block = validBlocks.get(Utility.random.nextInt(validBlocks.size()));  
        boss.getLivingEntity().teleport(block.getLocation());
        useCooldown(boss);

	        
		block.setType(Material.BEDROCK);
		final Location loc = block.getLocation();
	    final World world = block.getWorld();    
	    Likeaboss.scheduler.scheduleSyncDelayedTask(Likeaboss.in, new Runnable() {
	        public void run() {
	            world.getBlockAt(loc).setType(Material.AIR);
	            world.createExplosion(loc.getX(), loc.getY(), loc.getZ(), radius, fire, destroyWorld);
	        }
	    }, fuse);
	        
	    useCooldown(boss);
	    sendAreaMessage(boss, livingEntity);        
    }
}
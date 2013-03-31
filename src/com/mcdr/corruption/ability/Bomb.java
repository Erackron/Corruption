package com.mcdr.corruption.ability;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.util.Utility;

public class Bomb extends Ability {
	private int fuse = 80;
	private float radius = 3F;
	private boolean destroyWorld = true;
	private boolean fire = true;
	protected double activationChance = 10.0;
    
	public Bomb clone() {
		Bomb bomb = new Bomb();
		copySettings(bomb);
		bomb.setFuseTicks(this.fuse);
		bomb.setRadius(this.radius);
		bomb.setDestroyWorld(this.destroyWorld);
		bomb.setFire(this.fire);
		return bomb;
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
    
    /**
     * OnDeath Execute
     */
    public void Execute(LivingEntity livingEntity, Location lastLoc, Boss boss){
    	super.Execute(livingEntity, lastLoc, boss);
    	
    	plantBomb(livingEntity.getLocation());
    	
	    sendAreaMessage(lastLoc, boss.getName(), livingEntity);
    }
    
    /**
     * Normal Execute
     */
    public void Execute(LivingEntity livingEntity, Boss boss) {
		super.Execute(livingEntity, boss);
	    
		plantBomb(livingEntity.getLocation());
	        
	    useCooldown(boss);
	    sendAreaMessage(boss, livingEntity);
    }
    
    private void plantBomb(Location centerLoc){
    	final List<Block> validBlocks = findValidBlocks(centerLoc, 0, 3);
	    
	    if (validBlocks.isEmpty())
	    	return;
	    
        Block block = validBlocks.get(Utility.random.nextInt(validBlocks.size()));  
	        
		block.setType(Material.BEDROCK);
		final Location loc = block.getLocation();
	    final World world = block.getWorld();    
	    Corruption.scheduler.scheduleSyncDelayedTask(Corruption.in, new Runnable() {
	        public void run() {
	            world.getBlockAt(loc).setType(Material.AIR);
	            world.createExplosion(loc.getX(), loc.getY(), loc.getZ(), radius, fire, destroyWorld);
	        }
	    }, fuse);
    }
}
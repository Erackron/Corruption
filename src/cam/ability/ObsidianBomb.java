package cam.ability;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import cam.Likeaboss;
import cam.entity.Boss;

public class ObsidianBomb extends Ability {
	
	private int fuse = 80;
	private float radius = 3F;
	
    public ObsidianBomb(){
		activationConditions.add(ActivationCondition.ONATTACK);
	}
    
    public void setFuseTicks(int fuseticks){
    	this.fuse = fuseticks;
    }
    
    public void setRadius(float radius){
    	this.radius = radius;
    }
    
    public void Execute(EntityDamageEvent event, LivingEntity livingEntity, Boss boss) {
        // Grab the target, or a random player.
                
        final World world = livingEntity.getWorld();
        final Location loc = livingEntity.getLocation();
        
        Block b = world.getBlockAt(loc); 
        b.setType(Material.OBSIDIAN);
        
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(Likeaboss.instance, new Runnable() {
            public void run() {
                                
                world.getBlockAt(loc).breakNaturally();
                world.createExplosion(loc, radius);
            }
        }, fuse);
        
    }
}
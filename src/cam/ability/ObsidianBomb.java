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
		
    /**
     * How many ticks before the bomb goes off.
     */
    private final int FUSE = 80;
    
    public ObsidianBomb(){
		activationConditions.add(ActivationCondition.ONATTACK);		
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
                world.createExplosion(loc, 3F);
            }
        }, FUSE);
        
    }
}
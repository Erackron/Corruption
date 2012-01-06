package cam.boss;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitScheduler;

import cam.Likeaboss;
import cam.WorldEntitiesGetter;


public class BossTask {
	
	protected static Likeaboss plugin = null;
	protected static BossManager bossManager = null;
	protected static Object[] tempBosses = null;
	protected static BukkitScheduler bukkitScheduler;
	
	protected BossTask() {
	}
	
	public BossTask(Likeaboss plugin, BossManager bossManager) {
		BossTask.plugin = plugin;
		BossTask.bossManager = bossManager;
	}
	
	public void Start(double intervalCheckEntityHealth, double intervalCheckEntityExistence) {
		bukkitScheduler = plugin.getServer().getScheduler();
		
		if (intervalCheckEntityHealth > 0)
			bukkitScheduler.scheduleAsyncRepeatingTask(plugin, new CheckHealthTask(), 0, (long) (intervalCheckEntityHealth * 20));
		if (intervalCheckEntityExistence > 0)
			bukkitScheduler.scheduleAsyncRepeatingTask(plugin, new CheckEntityExistence(), 0, (long) (intervalCheckEntityExistence * 20));
	}
	
	public void Stop() {
		bukkitScheduler.cancelAllTasks();
	}
}

class CheckHealthTask extends BossTask implements Runnable {

	public void run() {
		tempBosses = bossManager.getBosses().toArray();
		
		for (Object object : tempBosses) {
			Boss boss = (Boss) object;
			LivingEntity livingEntity = boss.getLivingEntity();
			int entityHealth = livingEntity.getHealth();
			int entityMaxHealth = livingEntity.getMaxHealth();
			
			//If the entity received damage, but shouldn't
			//Needed because .damage() don't trigger an event, so other plugins may do something bad
			if (entityHealth < entityMaxHealth && boss.getHealth() > 0 && entityHealth > 0)
				livingEntity.setHealth(entityMaxHealth);
		}
	}
}

class CheckEntityExistence extends BossTask implements Runnable {

	public void run() {
		tempBosses = bossManager.getBosses().toArray();
		
		BukkitScheduler bukkitScheduler = plugin.getServer().getScheduler();
		Future<Set<Entity>> futur = bukkitScheduler.callSyncMethod(plugin, new WorldEntitiesGetter(plugin));
		Set<Entity> entities = new HashSet<Entity>();
		
		try {
			entities = futur.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
				
		for (Object object : tempBosses) {
			Boss boss = ((Boss) object);
			
			if (!entities.contains(boss.getLivingEntity()))
				bossManager.RemoveBoss(boss, false);
		}
	}
}

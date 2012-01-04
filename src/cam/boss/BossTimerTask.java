package cam.boss;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitScheduler;

import cam.EntitiesGetter;
import cam.Likeaboss;

public class BossTimerTask implements Runnable {
	
	private Likeaboss plugin = null;
	private BossManager bossManager = null;
	private Object[] tempBosses = null;
	
	public BossTimerTask(Likeaboss plugin, BossManager bossManager) {
		this.plugin = plugin;
		this.bossManager = bossManager;
	}
	
	public void run() {
		tempBosses = bossManager.getBosses().toArray();
		
		CheckBoss();
		CleanBoss();
	}

	private void CheckBoss() {
		for (Object object : tempBosses) {
			Boss boss = (Boss) object;
			LivingEntity livingEntity = boss.getLivingEntity();
			
			//If received damage, but not dead yet, and shouldn't receive damage
			//Needed because .damage() doesn't trigger an event
			if (livingEntity.getHealth() != livingEntity.getMaxHealth() && livingEntity.getHealth() > 0 && boss.getHealth() >= livingEntity.getHealth()) {
				livingEntity.setHealth(livingEntity.getMaxHealth());
			}
		}
	}
	
	private void CleanBoss() {
		BukkitScheduler bukkitScheduler = plugin.getServer().getScheduler();
		Future<Set<Entity>> futur = bukkitScheduler.callSyncMethod(plugin, new EntitiesGetter(plugin));
		Set<Entity> entities = new HashSet<Entity>();
		
		try {
			entities = futur.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		for (Object object : tempBosses) {
			LivingEntity livingEntity = ((Boss) object).getLivingEntity();
				
			if (!entities.contains(livingEntity))
				bossManager.RemoveBoss((Boss) object, false);
		}
	}
}

package cam.boss;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitScheduler;

import cam.Likeaboss;
import cam.WorldLivingEntitiesGetter;
import cam.config.LabConfig;

public class BossTaskManager {
	
	private Likeaboss plugin = null;
	private BukkitScheduler bukkitScheduler = null;
	private Set<Integer> taskIds = new HashSet<Integer>();
	
	public BossTaskManager(Likeaboss plugin) {
		this.plugin = plugin;
		BossTask.bossManager = plugin.getBossManager();
	}
	
	public void Start() {
		double drawEffectInterval = LabConfig.TasksData.BOSS_VISUAL_EFFECT.getValue();
		double checkEntityHealthInterval = LabConfig.TasksData.CHECK_ENTITY_HEALTH.getValue();
		double checkEntityExistenceInterval = LabConfig.TasksData.CHECK_ENTITY_EXISTENCE.getValue();
		bukkitScheduler = plugin.getServer().getScheduler();
		
		if (drawEffectInterval > 0)
			taskIds.add(bukkitScheduler.scheduleSyncRepeatingTask(plugin, new DrawEffect(), 0, (long) (drawEffectInterval * 20)));
		if (checkEntityHealthInterval > 0)
			taskIds.add(bukkitScheduler.scheduleAsyncRepeatingTask(plugin, new CheckHealthTask(), 0, (long) (checkEntityHealthInterval * 20)));
		if (checkEntityExistenceInterval > 0)
			taskIds.add(bukkitScheduler.scheduleAsyncRepeatingTask(plugin, new CheckEntityExistence(plugin, bukkitScheduler), 0, (long) (checkEntityExistenceInterval * 20)));
	}
	
	public void Stop() {
		for (int i : taskIds)
			bukkitScheduler.cancelTask(i);
		taskIds.clear();
	}
	
	public void Restart() {
		Stop();
		Start();
	}
}

abstract class BossTask {
	
	protected static BossManager bossManager = null;
	protected static Object[] tempBosses = null;
}

class DrawEffect extends BossTask implements Runnable {
	
	public DrawEffect() {
	}
	
	@Override
	public void run() {
		tempBosses = bossManager.getBosses().toArray();
		
		for (Object object : tempBosses) {
			LivingEntity livingEntity = ((Boss) object).getLivingEntity();
			World world = livingEntity.getWorld();
			
			world.playEffect(livingEntity.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
		}
	}
}

class CheckHealthTask extends BossTask implements Runnable {

	public CheckHealthTask() {
	}

	@Override
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
	
	private Likeaboss plugin = null;
	private BukkitScheduler bukkitScheduler = null;
	
	public CheckEntityExistence(Likeaboss plugin, BukkitScheduler bukkitScheduler) {
		this.plugin = plugin;
		this.bukkitScheduler = bukkitScheduler;
	}
	
	@Override
	public void run() {
		tempBosses = bossManager.getBosses().toArray();
		Future<Set<Entity>> futur = bukkitScheduler.callSyncMethod(plugin, new WorldLivingEntitiesGetter(plugin));
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

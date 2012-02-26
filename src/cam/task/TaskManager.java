package cam.task;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.scheduler.BukkitScheduler;

import cam.Likeaboss;
import cam.boss.BossManager;
import cam.config.TaskData;

public class TaskManager {
	
	private Likeaboss plugin = null;
	private BukkitScheduler bukkitScheduler = null;
	private Set<Integer> taskIds = new HashSet<Integer>();
	
	public TaskManager(Likeaboss plugin) {
		this.plugin = plugin;
		BossTask.bossManager = plugin.getBossManager();
	}
	
	public void Start() {
		double drawEffectInterval = TaskData.BOSS_VISUAL_EFFECT.getValue();
		double checkEntityHealthInterval = TaskData.CHECK_ENTITY_HEALTH.getValue();
		double checkEntityExistenceInterval = TaskData.CHECK_ENTITY_EXISTENCE.getValue();
		double checkBossProximity = TaskData.CHECK_BOSS_PROXIMITY.getValue();
		bukkitScheduler = plugin.getServer().getScheduler();
		
		if (drawEffectInterval > 0)
			taskIds.add(bukkitScheduler.scheduleSyncRepeatingTask(plugin, new DrawBossEffect(), 0, (long) (drawEffectInterval * 20)));
		if (checkEntityHealthInterval > 0)
			taskIds.add(bukkitScheduler.scheduleAsyncRepeatingTask(plugin, new CheckEntityHealth(), 0, (long) (checkEntityHealthInterval * 20)));
		if (checkEntityExistenceInterval > 0)
			taskIds.add(bukkitScheduler.scheduleAsyncRepeatingTask(plugin, new CheckEntityExistence(plugin, bukkitScheduler), 0, (long) (checkEntityExistenceInterval * 20)));
		if (checkBossProximity > 0)
			taskIds.add(bukkitScheduler.scheduleAsyncRepeatingTask(plugin, new CheckBossProximity(plugin), 0, (long) (checkBossProximity * 20)));
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

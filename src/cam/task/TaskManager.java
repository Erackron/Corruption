package cam.task;

import org.bukkit.scheduler.BukkitScheduler;

import cam.Likeaboss;
import cam.boss.Boss;
import cam.boss.BossManager;
import cam.config.GlobalConfig.TaskParam;

public class TaskManager {
	
	protected static BukkitScheduler bukkitScheduler;
	
	public TaskManager() {
		BaseTask.bossManager = Likeaboss.instance.getBossManager();
	}
	
	public void Start() {
		long retrieveBossList = (long) (TaskParam.RETRIEVE_BOSS_LIST.getValue() * 20);
		long drawBossEffect = (long) (TaskParam.BOSS_VISUAL_EFFECT.getValue() * 20);
		long checkEntityHealth = (long) (TaskParam.CHECK_ENTITY_HEALTH.getValue() * 20);
		long checkEntityExistence = (long) (TaskParam.CHECK_ENTITY_EXISTENCE.getValue() * 20);
		long checkBossProximity = (long) (TaskParam.CHECK_BOSS_PROXIMITY.getValue() * 20);
		long savePlayerFiles = (long) (TaskParam.SAVE_PLAYER_DATA.getValue() * 20);
		
		if (retrieveBossList > 0)
			bukkitScheduler.scheduleSyncRepeatingTask(Likeaboss.instance, new RetrieveBossList(), retrieveBossList, retrieveBossList);
		if (drawBossEffect > 0)
			bukkitScheduler.scheduleSyncRepeatingTask(Likeaboss.instance, new DrawBossEffect(), drawBossEffect, drawBossEffect);
		if (checkEntityHealth > 0)
			bukkitScheduler.scheduleAsyncRepeatingTask(Likeaboss.instance, new CheckEntityHealth(), checkEntityHealth, checkEntityHealth);
		if (checkEntityExistence > 0)
			bukkitScheduler.scheduleAsyncRepeatingTask(Likeaboss.instance, new CheckEntityExistence(), checkEntityExistence, checkEntityExistence);
		if (checkBossProximity > 0)
			bukkitScheduler.scheduleAsyncRepeatingTask(Likeaboss.instance, new CheckBossProximity(), checkBossProximity, checkBossProximity);
		if (savePlayerFiles > 0)
			bukkitScheduler.scheduleSyncRepeatingTask(Likeaboss.instance, new SavePlayerFiles(), savePlayerFiles, savePlayerFiles);
	}
	
	public void Stop() {
		bukkitScheduler.cancelTasks(Likeaboss.instance);
	}
	
	public void Restart() {
		Stop();
		Start();
	}
	
	public void setBukkitScheduler(BukkitScheduler bukkitScheduler) {
		TaskManager.bukkitScheduler = bukkitScheduler;
	}
}

class BaseTask {
	
	protected static BossManager bossManager;
	protected static Boss[] tempBosses = new Boss[0];
}
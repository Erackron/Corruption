package cam.task;

import cam.Likeaboss;
import cam.config.GlobalConfig.TaskParam;

public abstract class TaskManager {
	public static void Start() {
		ScheduleSyncRepeatingTask(new DrawBossEffect(), TaskParam.DRAW_BOSS_EFFECT.getValue());
		ScheduleSyncRepeatingTask(new CheckEntityHealth(), TaskParam.CHECK_ENTITY_HEALTH.getValue());
		ScheduleSyncRepeatingTask(new CheckEntityExistence(), TaskParam.CHECK_ENTITY_EXISTENCE.getValue());
		ScheduleSyncRepeatingTask(new CheckEntityProximity(), TaskParam.CHECK_ENTITY_PROXIMITY.getValue());
		ScheduleSyncRepeatingTask(new SavePlayerData(), TaskParam.SAVE_PLAYER_DATA.getValue());
	}
	
	private static void ScheduleSyncRepeatingTask(BaseTask baseTask, double period) {
		if (period > 0) {
			long periodInTicks = (long) (period * 20);
			Likeaboss.scheduler.scheduleSyncRepeatingTask(Likeaboss.instance, baseTask, periodInTicks, periodInTicks);
		}
	}
	
	public static void Stop() {
		Likeaboss.scheduler.cancelTasks(Likeaboss.instance);
	}
	
	public static void Restart() {
		Stop();
		Start();
	}
}

abstract class BaseTask implements Runnable {}

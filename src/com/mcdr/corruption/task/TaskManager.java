package com.mcdr.corruption.task;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.GlobalConfig.TaskParam;

public abstract class TaskManager {
	public static void Start() {
		ScheduleSyncRepeatingTask(new DrawBossEffect(), TaskParam.DRAW_BOSS_EFFECT.getValue());
		ScheduleSyncRepeatingTask(new CheckEntityExistence(), TaskParam.CHECK_ENTITY_EXISTENCE.getValue());
		ScheduleSyncRepeatingTask(new CheckEntityProximity(), TaskParam.CHECK_ENTITY_PROXIMITY.getValue());
		ScheduleSyncRepeatingTask(new SavePlayerData(), TaskParam.SAVE_PLAYER_DATA.getValue());
	}
	
	private static void ScheduleSyncRepeatingTask(BaseTask baseTask, double period) {
		if (period > 0) {
			long periodInTicks = (long) (period * 20);
			Corruption.scheduler.scheduleSyncRepeatingTask(Corruption.in, baseTask, periodInTicks, periodInTicks);
		}
	}
	
	public static void Stop() {
		Corruption.scheduler.cancelTasks(Corruption.in);
	}
	
	public static void Restart() {
		Stop();
		Start();
	}
}

abstract class BaseTask implements Runnable {}

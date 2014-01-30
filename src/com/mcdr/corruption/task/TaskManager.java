package com.mcdr.corruption.task;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.GlobalConfig.TaskParam;

public abstract class TaskManager {
	private static CorPlayerFileAccessor corPlayerFileAccessor = new CorPlayerFileAccessor();
	
	public static void start() {
		ScheduleSyncRepeatingTask(new DrawBossEffect(), TaskParam.DRAW_BOSS_EFFECT.getValue());
		ScheduleSyncRepeatingTask(new CheckEntityExistence(), TaskParam.CHECK_ENTITY_EXISTENCE.getValue());
		ScheduleSyncRepeatingTask(new CheckEntityProximity(), TaskParam.CHECK_ENTITY_PROXIMITY.getValue());
		ScheduleSyncRepeatingTask(new LoadPlayerData(), TaskParam.LOAD_PLAYER_DATA.getValue());
		ScheduleSyncRepeatingTask(new SavePlayerData(), TaskParam.SAVE_PLAYER_DATA.getValue());
		corPlayerFileAccessor.start();
    }

    private static void ScheduleSyncRepeatingTask(BaseTask baseTask, double period) {
        if (period > 0.0D) {
            long periodInTicks = (long)(period * 20.0D);
		      Corruption.scheduler.runTaskTimer(Corruption.in, baseTask, periodInTicks, periodInTicks);
		}
	}
	
	public static void stop() {
		corPlayerFileAccessor.stop();
	    corPlayerFileAccessor.join();
	    
		Corruption.scheduler.cancelTasks(Corruption.in);
	}
	
	public static void restart() {
		stop();
		start();
	}

	public static CorPlayerFileAccessor getCorPlayerFileAccessor() {
	    return corPlayerFileAccessor;
	}
}

abstract class BaseTask implements Runnable {}

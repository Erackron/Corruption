package cam.task;

import cam.boss.Boss;

public class RetrieveBossList extends BaseTask implements Runnable {
	
	@Override
	public void run() {
		tempBosses = bossManager.getBosses().toArray(new Boss[0]);
	}
}

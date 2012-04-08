package cam.task;

import cam.boss.Boss;

public class CheckEntityExistence extends BaseTask implements Runnable {
	
	@Override
	public void run() {
		for (Boss boss : tempBosses) {
			//Sometimes Entity.isDead() isn't enough, most certainly a MC/CraftBukkit bug.
			if (!boss.IsAlive())
				bossManager.RemoveBoss(boss);
		}
	}
}

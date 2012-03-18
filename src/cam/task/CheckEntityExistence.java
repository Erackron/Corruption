package cam.task;

import cam.boss.Boss;

class CheckEntityExistence extends BossTask implements Runnable {
	
	public CheckEntityExistence() {
	}
	
	@Override
	public void run() {
		tempBosses = bossManager.getBosses().toArray();
		
		for (Object object : tempBosses) {
			Boss boss = ((Boss) object);
			
			//Sometimes Entity.isDead() isn't enough, most certainly a MC/CraftBukkit bug.
			if (!boss.IsAlive())
				bossManager.RemoveBoss(boss);
		}
	}
}

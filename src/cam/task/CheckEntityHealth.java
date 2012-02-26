package cam.task;

import org.bukkit.entity.LivingEntity;

import cam.boss.Boss;

class CheckEntityHealth extends BossTask implements Runnable {

	public CheckEntityHealth() {
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

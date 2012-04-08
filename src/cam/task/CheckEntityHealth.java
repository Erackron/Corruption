package cam.task;

import org.bukkit.entity.LivingEntity;

import cam.boss.Boss;

public class CheckEntityHealth extends BaseTask implements Runnable {
	
	@Override
	public void run() {
		for (Boss boss : tempBosses) {
			LivingEntity livingEntity = boss.getLivingEntity();
			int entityHealth = livingEntity.getHealth();
			int entityMaxHealth = livingEntity.getMaxHealth();
			
			//If the entity received damage, but shouldn't (for example if another plugin uses LivingEntity.setHealth)
			if (entityHealth < entityMaxHealth && boss.getHealth() > 0 && entityHealth > 0)
				livingEntity.setHealth(entityMaxHealth);
		}
	}
}

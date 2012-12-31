package com.mcdr.likeaboss.task;

import org.bukkit.entity.LivingEntity;

import com.mcdr.likeaboss.entity.Boss;
import com.mcdr.likeaboss.entity.LabEntityManager;


public class CheckEntityHealth extends BaseTask {
	@Override
	public void run() {
		for (Boss boss : LabEntityManager.getBosses()) {
			LivingEntity livingEntity = boss.getLivingEntity();
			int entityHealth = livingEntity.getHealth();
			int entityMaxHealth = livingEntity.getMaxHealth();
			
			//If the entity received damage not handled by the EntityDamageEvent listener.
			if (entityHealth < entityMaxHealth && boss.getHealth() > 0)
				livingEntity.setHealth(entityMaxHealth);
		}
	}
}

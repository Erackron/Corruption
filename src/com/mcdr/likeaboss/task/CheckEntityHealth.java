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
			
			//If the entity received damage not handled by the EntityDamageEvent listener.
			if (entityHealth < boss.getHealth() && boss.getHealth() > 0)
				livingEntity.setHealth(boss.getHealth());
		}
	}
}

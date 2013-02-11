package com.mcdr.corruption.task;

import org.bukkit.entity.LivingEntity;

import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.entity.CorEntityManager;


public class CheckEntityHealth extends BaseTask {
	@Override
	public void run() {
		for (Boss boss : CorEntityManager.getBosses()) {
			LivingEntity livingEntity = boss.getLivingEntity();
			int entityHealth = livingEntity.getHealth();
			
			//If the entity received damage not handled by the EntityDamageEvent listener.
			if (entityHealth < boss.getHealth() && boss.getHealth() > 0)
				livingEntity.setHealth(boss.getHealth());
		}
	}
}

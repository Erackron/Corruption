package com.mcdr.likeaboss.task;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import com.mcdr.likeaboss.entity.Boss;
import com.mcdr.likeaboss.entity.LabEntityManager;


public class DrawBossEffect extends BaseTask {
	@Override
	public void run() {
		for (Boss boss : LabEntityManager.getBosses()) {
			LivingEntity livingEntity = boss.getLivingEntity();
			World world = livingEntity.getWorld();
			
			world.playEffect(livingEntity.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
		}
	}
}

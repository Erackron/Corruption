package com.mcdr.corruption.task;

import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.entity.CorEntityManager;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;


public class DrawBossEffect extends BaseTask {
    @Override
    public void run() {
        for (Boss boss : CorEntityManager.getBosses()) {
            LivingEntity livingEntity = boss.getLivingEntity();
            World world = livingEntity.getWorld();

            world.playEffect(livingEntity.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
        }
    }
}

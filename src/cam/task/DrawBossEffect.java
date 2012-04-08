package cam.task;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import cam.boss.Boss;

public class DrawBossEffect extends BaseTask implements Runnable {
	
	@Override
	public void run() {
		for (Boss boss : tempBosses) {
			LivingEntity livingEntity = boss.getLivingEntity();
			World world = livingEntity.getWorld();
			
			world.playEffect(livingEntity.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
		}
	}
}

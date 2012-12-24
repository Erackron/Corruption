package cam.task;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import cam.entity.Boss;
import cam.entity.LabEntityManager;

public class DrawBossEffect extends BaseTask {
	public void run() {
		for (Boss boss : LabEntityManager.getBosses()) {
			LivingEntity livingEntity = boss.getLivingEntity();
			World world = livingEntity.getWorld();
			
			world.playEffect(livingEntity.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
		}
	}
}

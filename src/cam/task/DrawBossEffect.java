package cam.task;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import cam.boss.Boss;

class DrawBossEffect extends BossTask implements Runnable {
	
	public DrawBossEffect() {
	}
	
	@Override
	public void run() {
		tempBosses = bossManager.getBosses().toArray();
		
		for (Object object : tempBosses) {
			LivingEntity livingEntity = ((Boss) object).getLivingEntity();
			World world = livingEntity.getWorld();
			
			world.playEffect(livingEntity.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
		}
	}
}

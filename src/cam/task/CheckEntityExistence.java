package cam.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitScheduler;

import cam.Likeaboss;
import cam.boss.Boss;

class CheckEntityExistence extends BossTask implements Runnable {
	
	private Likeaboss plugin = null;
	private BukkitScheduler bukkitScheduler = null;
	
	public CheckEntityExistence(Likeaboss plugin, BukkitScheduler bukkitScheduler) {
		this.plugin = plugin;
		this.bukkitScheduler = bukkitScheduler;
	}
	
	@Override
	public void run() {
		tempBosses = bossManager.getBosses().toArray();
		Future<Set<LivingEntity>> futur = bukkitScheduler.callSyncMethod(plugin, new WorldLivingEntitiesGetter(plugin));
		Set<LivingEntity> entities = new HashSet<LivingEntity>();
		
		try {
			entities = futur.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
				
		for (Object object : tempBosses) {
			Boss boss = ((Boss) object);
			
			if (!entities.contains(boss.getLivingEntity()))
				bossManager.RemoveBoss(boss);
		}
	}
	
	class WorldLivingEntitiesGetter implements Callable<Set<LivingEntity>> {

		Likeaboss plugin = null;
		
		public WorldLivingEntitiesGetter(Likeaboss plugin) {
			this.plugin = plugin;
		}
		
		@Override
		public Set<LivingEntity> call() {
			Set<LivingEntity> entities = new HashSet<LivingEntity>();
			List<World> worlds = plugin.getServer().getWorlds();
			
			for (World world : worlds)
				entities.addAll(world.getLivingEntities());
			
			return entities;
		}
	}
}

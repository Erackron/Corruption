package cam.boss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import cam.Likeaboss;

public class BossManager {
	
	private Map<World, Map<Class<? extends LivingEntity>, BossParams>> bossesParams = new HashMap<World, Map<Class<? extends LivingEntity>, BossParams>>();
	private List<Boss> bosses = new ArrayList<Boss>();
	private Likeaboss plugin = null;
	private DropManager dropManager = null;
	private int bossKilled = 0;
	
	public BossManager(Likeaboss plugin) {
		this.plugin = plugin;
		this.dropManager = plugin.getDropManager();
	}
	
	public void AddBossParameters(World world, Class<? extends LivingEntity> type, double[] spawnValues, double[] statsValues) {
		Map<Class<? extends LivingEntity>, BossParams> map = bossesParams.get(world);
		
		if (bossesParams.get(world) != null)
			map.put(type, new BossParams(spawnValues, statsValues));
		else {
			map = new HashMap<Class<? extends LivingEntity>, BossParams>();
			
			map.put(type, new BossParams(spawnValues, statsValues));
			bossesParams.put(world, map);
		}
	}
	
	public Boss AddBoss(LivingEntity livingEntity) {
		Boss boss = new Boss(livingEntity, getBossParams(livingEntity));
		bosses.add(boss);
		
		return boss;
	}
	
	public void RemoveBoss(Boss boss, boolean killed) {
		bosses.remove(boss);
		
		if (killed) {
			LivingEntity livingEntity = boss.getLivingEntity();
			World world = livingEntity.getWorld();
			Location location = livingEntity.getLocation();
			
			dropManager.Drop(world, location);
			
			bossKilled++;
		}
	}
		
	public void DamageBoss(Boss boss, int damage) {
		boss.setHealth(boss.getHealth() - damage);
		boss.setLastDamage(damage);
	}
	
	public boolean IsDead(Boss boss) {
		if (boss.getHealth() <= 0)
			return true;
		return false;
	}
	
	public void Clear() {
		bossKilled = 0;
		bosses.clear();
	}
	
	public Boss getBoss(Entity entity) {
		for (Boss boss : bosses) {
			if (boss.getLivingEntity() == entity)
				return boss;
		}
		
		return null;
	}
	
	public BossParams getBossParams(LivingEntity livingEntity) {
		Map<Class<? extends LivingEntity>, BossParams> map = bossesParams.get(livingEntity.getWorld());
		
		if (map == null) {
			plugin.getLabConfig().LoadFiles();
			map = bossesParams.get(livingEntity.getWorld());
		}
		
		Class<? extends LivingEntity> livingEntityClass = livingEntity.getClass();
		
		for (Class<? extends LivingEntity> type : map.keySet()) {
			if (livingEntityClass.equals(type)) 
				return map.get(type);
		}
		
		return null; //Should never happen
	}
	

	public Map<World, Map<Class<? extends LivingEntity>, BossParams>> getBossesParams() {
		return bossesParams;
	}
	
	public List<Boss> getBosses() {
		return bosses;		
	}
		
	public int getBossKilled() {
		return bossKilled;
	}
	
	public class BossParams {
		
		private double chance = 0;
		private double maxHeight = 0;
		private double fromMobSpawner = 0;
		private double healthCoef = 0;
		private double damageCoef = 0;
		private double expCoef = 0;
		
		public BossParams(double[] spawnValues, double[] statsValues) {
			this.chance = spawnValues[0];
			this.maxHeight = spawnValues[1];
			this.fromMobSpawner = spawnValues[2];
			this.healthCoef = statsValues[0];
			this.damageCoef = statsValues[1];
			this.expCoef = statsValues[2];
		}
		
		public double getChance() {
			return chance;
		}
		
		public double getMaxHeight() {
			return maxHeight;
		}
		
		public double getFromMobSpawner() {
			return fromMobSpawner;
		}

		public double getHealthCoef() {
			return healthCoef;
		}

		public double getDamageCoef() {
			return damageCoef;
		}

		public double getExpCoef() {
			return expCoef;
		}
	}
}

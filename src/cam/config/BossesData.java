package cam.config;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftBlaze;
import org.bukkit.craftbukkit.entity.CraftCaveSpider;
import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.craftbukkit.entity.CraftEnderman;
import org.bukkit.craftbukkit.entity.CraftGhast;
import org.bukkit.craftbukkit.entity.CraftGiant;
import org.bukkit.craftbukkit.entity.CraftMagmaCube;
import org.bukkit.craftbukkit.entity.CraftPigZombie;
import org.bukkit.craftbukkit.entity.CraftSilverfish;
import org.bukkit.craftbukkit.entity.CraftSkeleton;
import org.bukkit.craftbukkit.entity.CraftSlime;
import org.bukkit.craftbukkit.entity.CraftSpider;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.LivingEntity;

public enum BossesData {
	
	CREEPER (CraftCreeper.class, BossesSpawnData.CREEPER, BossesStatsData.CREEPER),
	MAGMACUBE (CraftMagmaCube.class, BossesSpawnData.MAGMACUBE, BossesStatsData.MAGMACUBE),
	GIANT (CraftGiant.class, BossesSpawnData.GIANT, BossesStatsData.GIANT),
	PIGZOMBIE (CraftPigZombie.class, BossesSpawnData.PIGZOMBIE, BossesStatsData.PIGZOMBIE),
	SLIME (CraftSlime.class, BossesSpawnData.SLIME, BossesStatsData.SLIME),
	SILVERFISH (CraftSilverfish.class, BossesSpawnData.SILVERFISH, BossesStatsData.SILVERFISH),
	SPIDER (CraftSpider.class, BossesSpawnData.SPIDER, BossesStatsData.SPIDER),
	ENDERMAN (CraftEnderman.class, BossesSpawnData.ENDERMAN, BossesStatsData.ENDERMAN),
	BLAZE (CraftBlaze.class, BossesSpawnData.BLAZE, BossesStatsData.BLAZE),
	CAVESPIDER (CraftCaveSpider.class, BossesSpawnData.CAVESPIDER, BossesStatsData.CAVESPIDER),
	ZOMBIE (CraftZombie.class, BossesSpawnData.ZOMBIE, BossesStatsData.ZOMBIE),
	GHAST (CraftGhast.class, BossesSpawnData.GHAST, BossesStatsData.GHAST),
	SKELETON (CraftSkeleton.class, BossesSpawnData.SKELETON, BossesStatsData.SKELETON);
	
	private Class<? extends LivingEntity> type = null;
	private BossesSpawnData spawnData = null;
	private BossesStatsData statsData = null;
	
	private BossesData(Class<? extends LivingEntity> type, BossesSpawnData spawnData, BossesStatsData statsData) {
		this.type = type;
		this.spawnData = spawnData;
		this.statsData = statsData;
	}
	
	public Class<? extends LivingEntity> getType() {
		return type;
	}
	
	public BossesSpawnData getSpawnData() {
		return spawnData;
	}
	
	public BossesStatsData getStatsData() {
		return statsData;
	}

	public static enum BossesSpawnData {
		
		CREEPER ("Boss.Creeper.Spawn", 3.0, 128, 0),
		MAGMACUBE ("Boss.MagmaCube.Spawn", 3.0, 128, 0),
		GIANT ("Boss.Giant.Spawn", 0.0, 0, 0),
		PIGZOMBIE ("Boss.PigZombie.Spawn", 3.0, 128, 0),
		SLIME ("Boss.Slime.Spawn", 3.0, 128, 0),
		SILVERFISH ("Boss.Silverfish.Spawn", 3.0, 128, 0),
		SPIDER ("Boss.Spider.Spawn", 3.0, 128, 0),
		ENDERMAN ("Boss.Enderman.Spawn", 3.0, 128, 0),
		BLAZE ("Boss.Blaze.Spawn", 3.0, 128, 0),
		CAVESPIDER ("Boss.CaveSpider.Spawn", 3.0, 128, 0),
		ZOMBIE ("Boss.Zombie.Spawn", 3.0, 128, 0),
		GHAST ("Boss.Ghast.Spawn", 3.0, 128, 0),
		SKELETON ("Boss.Skeleton.Spawn", 3.0, 128, 0);
		
		private String line = null;
		private List<Double> params = new LinkedList<Double>();
		
		private BossesSpawnData(String line, double chance, double maxHeight, double fromMobSpawner) {
			this.line = line;
			params.add(chance);
			params.add(maxHeight);
			params.add(fromMobSpawner);
		}
		
		public String getLine() {
			return line;
		}
	
		public String getStringValues() {
			String values = "";
			
			Iterator<Double> it = params.iterator();
			while (it.hasNext())
				values += String.valueOf(it.next()) + ' ';
			values = values.substring(0, values.length() - 1);
			
			return values;
		}
		
		public List<Double> getValues() {
			return params;
		}
	}
	
	public static enum BossesStatsData {
		
		CREEPER ("Boss.Creeper.Stats", 3.75, 3.0, 10.5),
		MAGMACUBE ("Boss.MagmaCube.Stats", 5.5, 3.0, 8.0),
		GIANT ("Boss.Giant.Stats", 0.0, 0.0, 0.0),
		PIGZOMBIE ("Boss.PigZombie.Stats", 5.5, 3.0, 8.0),
		SLIME ("Boss.Slime.Stats", 5.5, 3.0, 8.0),
		SILVERFISH ("Boss.Silverfish.Stats", 10.0, 10.0, 7.0),
		SPIDER ("Boss.Spider.Stats", 5.5, 3.0, 8.0),
		ENDERMAN ("Boss.Enderman.Stats", 3.75, 3.0, 10.5),
		BLAZE ("Boss.Blaze.Stats", 5.5, 3.0, 8.0),
		CAVESPIDER ("Boss.CaveSpider.Stats", 5.5, 3.0, 8.0),
		ZOMBIE ("Boss.Zombie.Stats", 5.5, 3.0, 8.0),
		GHAST ("Boss.Ghast.Stats", 5.5, 3.0, 8.0),
		SKELETON ("Boss.Skeleton.Stats", 5.5, 3.0, 8.0);
		
		private String line = null;
		private List<Double> params = new LinkedList<Double>();
		
		private BossesStatsData(String line, double healthCoef, double damageCoef, double expCoef) {
			this.line = line;
			params.add(healthCoef);
			params.add(damageCoef);
			params.add(expCoef);
		}
		
		public String getLine() {
			return line;
		}
		
		public String getStringValues() {
			String values = "";
			
			Iterator<Double> it = params.iterator();
			while (it.hasNext())
				values += String.valueOf(it.next()) + ' ';
			values = values.substring(0, values.length() - 1);
			
			return values;
		}
		
		public List<Double> getValues() {
			return params;
		}
	}
}

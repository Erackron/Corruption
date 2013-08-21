package com.mcdr.corruption.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;
import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.entity.data.PigZombieBossData;
import com.mcdr.corruption.entity.data.SkeletonBossData;
import com.mcdr.corruption.entity.data.SlimeBossData;
import com.mcdr.corruption.entity.data.ZombieBossData;

public abstract class CorEntityManager {
	private static List<Boss> bosses = new ArrayList<Boss>();
	private static List<EntityType> bossEntityTypes = new ArrayList<EntityType>();
	
	public static Boss spawnBossEntity(Location location, EntityType entityType, BossData bossData){
		LivingEntity spawnedCreature;
		World world = location.getWorld();
		Entity spawnedEntity = world.spawnEntity(location, entityType);
		if (spawnedEntity.isValid())
			spawnedCreature = (LivingEntity) spawnedEntity;
		else
			return null;
		
		adjustSpecificEntities(spawnedCreature, bossData, entityType);
		
		Boss boss = new Boss(spawnedCreature, bossData);
		
		addBoss(boss);
		return boss;
	}
	
	public static void adjustSpecificEntities(LivingEntity livingEntity, BossData bossData, EntityType entityType){
		//Check and set the size of a slime
			if (Slime.class.isAssignableFrom(entityType.getEntityClass())) {
				Slime slime = (Slime) livingEntity;
				slime.setSize(((SlimeBossData) bossData).getMaximumSize());
			}
			
			//Check and set if it has to be a baby or villager zombie
			if (Zombie.class.isAssignableFrom(entityType.getEntityClass())) {
				Zombie zombie = (Zombie) livingEntity;
				zombie.setBaby(((ZombieBossData) bossData).isBaby());
				zombie.setVillager(((ZombieBossData) bossData).isVillager());
				if(PigZombie.class.isAssignableFrom(entityType.getEntityClass())){
					PigZombie pigZombie = (PigZombie) zombie;
					pigZombie.setAngry(((PigZombieBossData) bossData).isAngry());
				}
			}
			
			//Check and set if it has to be a normal or wither skeleton
			if (Skeleton.class.isAssignableFrom(entityType.getEntityClass())) {
				Skeleton skeleton = (Skeleton) livingEntity;
				skeleton.setSkeletonType(((SkeletonBossData) bossData).getSkeletonType());
			}
	}
	
	public static void addBoss(Boss boss) {
		boss.getLivingEntity().setMetadata("isBoss", new FixedMetadataValue(Corruption.in, boss.getRawName()));
		bosses.add(boss);
	}
	
	public static void damageBoss(Boss boss, double damageTaken) {
		boss.setHealth(boss.getHealth() - damageTaken);
	}
	
	public static boolean isDead(Boss boss) {
		if (boss.getHealth() <= 0)
			return true;
		return false;
	}
	
	public static void clear() {
		bosses.clear();
	}
	
	public static Boss getBoss(Entity entity) {
		for (Boss boss : bosses) {
			if (boss.getLivingEntity() == entity)
				return boss;
		}
		
		return null;
	}
	
	public static List<Boss> getBosses() {
		return bosses;
	}
	
	public static List<EntityType> getBossEntityTypes(){
		for(Boss boss : bosses)
			bossEntityTypes.add(boss.getLivingEntity().getType());
		return bossEntityTypes;
	}
	
	public static CorEntity getEntity(LivingEntity livingEntity) {
		return getBoss(livingEntity);		
	}

	public static void purgeAllBosses() {
	    for (Boss boss: bosses){
	      boss.getLivingEntity().remove();
	    }
	    bosses.clear();
	}
	
	public static void purgeBosses(World world){
		List<Boss> removeList = new ArrayList<Boss>();
		for (Boss boss: bosses){
			if(world.getName().equalsIgnoreCase(boss.livingEntity.getWorld().getName())){
				removeList.add(boss);
				boss.livingEntity.remove();
			}
		}
		bosses.removeAll(removeList);
	}
	
	public static void purgeBosses(Collection<Boss> bosses){
		for(Boss boss: bosses){
			boss.livingEntity.remove();
		}
		CorEntityManager.bosses.removeAll(bosses);
	}
}

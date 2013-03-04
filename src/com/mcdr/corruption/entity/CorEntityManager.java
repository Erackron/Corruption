package com.mcdr.corruption.entity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public abstract class CorEntityManager {
	private static List<Boss> bosses = new ArrayList<Boss>();
	private static List<EntityType> bossEntityTypes = new ArrayList<EntityType>();
	
	public static void AddBoss(Boss boss) {
		bosses.add(boss);
	}
	
	public static void DamageBoss(Boss boss, int damage) {
		boss.setHealth(boss.getHealth() - damage);
	}
	
	public static boolean IsDead(Boss boss) {
		if (boss.getHealth() <= 0)
			return true;
		return false;
	}
	
	public static void Clear() {
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
}

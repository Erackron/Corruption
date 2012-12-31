<<<<<<< HEAD:src/com/mcdr/likeaboss/entity/LabEntityManager.java
package com.mcdr.likeaboss.entity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public abstract class LabEntityManager {
	private static List<Boss> bosses = new ArrayList<Boss>();
	
	public static void AddBoss(Boss boss) {
		bosses.add(boss);
	}
	
	public static void RemoveBoss(Boss boss) {
		bosses.remove(boss);
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
	
	public static LabEntity getEntity(LivingEntity livingEntity) {
		return getBoss(livingEntity);		
	}
}
=======
package com.mcdr.likeaboss.entity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public abstract class LabEntityManager {
	private static List<Boss> bosses = new ArrayList<Boss>();
	
	public static void AddBoss(Boss boss) {
		bosses.add(boss);
	}
	
	public static void RemoveBoss(Boss boss) {
		bosses.remove(boss);
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
	
	public static LabEntity getEntity(LivingEntity livingEntity) {
		return getBoss(livingEntity);		
	}
}
>>>>>>> origin/EquipmentExpansion:src/com/mcdr/likeaboss/entity/LabEntityManager.java

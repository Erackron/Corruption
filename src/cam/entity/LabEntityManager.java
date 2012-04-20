package cam.entity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public abstract class LabEntityManager {
	private static List<Boss> bosses = new ArrayList<Boss>();
	private static List<Minion> minions = new ArrayList<Minion>();
	
	public static void AddBoss(Boss boss) {
		bosses.add(boss);
	}
	
	public static void AddMinion(Minion minion) {
		minions.add(minion);
	}
	
	public static void RemoveBoss(Boss boss) {
		bosses.remove(boss);
	}
	
	public static void RemoveMinion(Minion minion) {
		minions.remove(minion);
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
		minions.clear();
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
	
	public static Minion getMinion(Entity entity) {
		for (Minion minion : minions) {
			if (minion.getLivingEntity() == entity)
				return minion;
		}
		
		return null;
	}
	
	public static List<Minion> getMinions() {
		return minions;
	}
	
	public static LabEntity getEntity(LivingEntity livingEntity) {
		LabEntity labEntity = getBoss(livingEntity);
		
		if (labEntity == null)
			labEntity = getMinion(livingEntity);
		
		return labEntity;
	}
}

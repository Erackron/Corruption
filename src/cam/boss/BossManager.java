package cam.boss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class BossManager {
	
	private static List<Boss> bosses = new ArrayList<Boss>();
	private Map<Material, Integer> droped = new HashMap<Material, Integer>();
	private int bossKilled = 0;
	
	public BossManager() {
	}
	
	public void AddBoss(Boss boss) {
		bosses.add(boss);
	}
	
	public Boss AddBoss(LivingEntity livingEntity) {
		Boss boss = new Boss(livingEntity);
		bosses.add(boss);
		
		return boss;
	}
	
	public void AddDroped(Material material, int quantity) {
		int value = 0;
		if (droped.containsKey(material))
			value = droped.get(material);			
		droped.put(material, value + quantity);
	}
	
	public void RemoveBoss(Entity entity, boolean killed) {
		for (Boss boss : bosses) {
			if (boss.getLivingEntity() == entity) {
				bosses.remove(boss);
				if (killed)
					bossKilled++;
				break;
			}
		}
	}
	
	public void RemoveBoss(Boss boss, boolean killed) {
		bosses.remove(boss);
		if (killed)
			bossKilled++;
	}
	
	public boolean IsBoss(Entity entity) {
		for (Boss bossa : bosses) {
			if (bossa.getLivingEntity() == entity)
				return true;
		}
		return false;
	}
	
	public boolean IsDead(Boss boss) {
		if (boss.getHealth() <= 0)
			return true;
		return false;
	}
	
	public void DamageBoss(Boss boss, int damage) {
		boss.setHealth(boss.getHealth() - damage);
		if (boss.getHealth() <= 0)
			boss.setHealth(0);
	}

	public Boss getBoss(Entity entity) {
		for (Boss boss : bosses) {
			if (boss.getLivingEntity() == entity)
				return boss;
		}
		return null; //Should never happen
	}
	
	public List<Boss> getBosses() {
		return bosses;		
	}
	
	public Map<Material, Integer> getDroped() {
		return droped;
	}
	
	public int getBossKilled() {
		return bossKilled;
	}
	
	public int getBossCount() {
		return bosses.size();
	}
	
	public void clear() {
		bossKilled = 0;
		bosses.clear();
		droped.clear();
	}
}
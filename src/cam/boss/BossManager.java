package cam.boss;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import cam.config.BossData;

public class BossManager {
	
	private List<Boss> bosses = new ArrayList<Boss>();
	private int bossKilled = 0;
	
	public BossManager() {
	}
	
	public Boss AddBoss(LivingEntity livingEntity, BossData bossData) {
		Boss boss = new Boss(livingEntity, bossData);
		bosses.add(boss);
		
		return boss;
	}
	
	public void KillBoss(Boss boss) {
		bossKilled++;
		RemoveBoss(boss);
	}
	
	public void RemoveBoss(Boss boss) {
		bosses.remove(boss);
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
		
	public List<Boss> getBosses() {
		return bosses;		
	}
		
	public int getBossKilled() {
		return bossKilled;
	}
}

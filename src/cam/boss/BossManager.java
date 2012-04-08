package cam.boss;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;

public class BossManager {
	
	private List<Boss> bosses = new ArrayList<Boss>();
	
	public void AddBoss(Boss boss) {
		bosses.add(boss);
	}
	
	public void KillBoss(Boss boss) {
		RemoveBoss(boss);
	}
	
	public void RemoveBoss(Boss boss) {
		bosses.remove(boss);
	}
		
	public void DamageBoss(Boss boss, int damage) {
		boss.setHealth(boss.getHealth() - damage);
	}
	
	public boolean IsDead(Boss boss) {
		if (boss.getHealth() <= 0)
			return true;
		return false;
	}
	
	public void Clear() {
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
}

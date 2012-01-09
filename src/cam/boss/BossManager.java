package cam.boss;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class BossManager {
	
	private List<Boss> bosses = new ArrayList<Boss>();
	private DropManager dropManager = null;
	private int bossKilled = 0;
	
	public BossManager(DropManager dropManager) {
		this.dropManager = dropManager;
	}
	
	public Boss AddBoss(LivingEntity livingEntity) {
		Boss boss = new Boss(livingEntity);
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
	
	public boolean IsBoss(Entity entity) {
		for (Boss boss : bosses) {
			if (boss.getLivingEntity() == entity)
				return true;
		}
		return false;
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
		bossKilled = 0;
		bosses.clear();
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
		
	public int getBossKilled() {
		return bossKilled;
	}
}

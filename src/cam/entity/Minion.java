package cam.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;

public class Minion extends LabEntity {
	private Boss boss;
	
	public Minion(LivingEntity livingEntity, Boss boss) {
		this.livingEntity = livingEntity;
		this.boss = boss;
	}
	
	public Boss getBoss() {
		return boss;
	}
	
	@Override
	public void OnDeath(EntityDeathEvent event) {
		event.setDroppedExp(0);
		event.getDrops().clear();
		LabEntityManager.RemoveMinion(this);
	}
}

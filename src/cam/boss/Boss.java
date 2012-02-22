package cam.boss;

import org.bukkit.entity.LivingEntity;

import cam.config.BossData;

public class Boss {
	
	private LivingEntity livingEntity = null;
	private BossData bossData = null;
	private int health = 0;
	private int lastDamage = 0;
	private boolean found = false;
	private int lastTimeNotified = 0;

	public Boss(LivingEntity livingEntity, BossData bossData) {
		this.livingEntity = livingEntity;
		this.bossData = bossData;
		this.health = (int) (livingEntity.getMaxHealth() * bossData.getHealthCoef());
	}
	
	public LivingEntity getLivingEntity() {
		return livingEntity;
	}
	
	public BossData getBossData() {
		return bossData;
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getLastDamage() {
		return lastDamage;
	}
	
	public boolean getFound() {
		return found;
	}
	
	public int getLastTimeNotified() {
		return lastTimeNotified;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public void setLastDamage(int lastDamage) {
		this.lastDamage = lastDamage;
	}
	
	public void setFound(boolean found) {
		this.found = found;
	}

	public void setLastTimeNotified(int lastTimeNotified) {
		this.lastTimeNotified = lastTimeNotified;
	}
}

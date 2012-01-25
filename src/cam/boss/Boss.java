package cam.boss;

import org.bukkit.entity.LivingEntity;

import cam.boss.BossManager.BossParams;

public class Boss {
	
	private double healthCoef = 0;
	private double damageCoef = 0;
	private double expCoef = 0;
	
	private LivingEntity livingEntity = null;
	private int health = 0;
	private int lastDamage = 0;
	private boolean found = false;
	private int lastTimeNotified = 0;

	public Boss(LivingEntity livingEntity, BossParams bossParams) {
		this.livingEntity = livingEntity;
		this.healthCoef = bossParams.getHealthCoef();
		this.damageCoef = bossParams.getDamageCoef();
		this.expCoef = bossParams.getExpCoef();
		this.health = (int) (livingEntity.getMaxHealth() * healthCoef);
	}
	
	public double getHealthCoef() {
		return healthCoef;
	}
	
	public double getDamageCoef() {
		return damageCoef;
	}
	
	public double getExpCoef() {
		return expCoef;
	}
	
	public LivingEntity getLivingEntity() {
		return livingEntity;
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

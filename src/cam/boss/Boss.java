package cam.boss;

import org.bukkit.entity.LivingEntity;

import cam.Config;

public class Boss {
	
	private double healthCoef;
	private double damageCoef;
	private double expCoef;
	
	private LivingEntity livingEntity;
	private int health;
	private boolean found = false;

	public Boss(Config config, LivingEntity livingEntity) {
		healthCoef = config.getValue("Boss.HealthCoef");
		damageCoef = config.getValue("Boss.DamageCoef");
		expCoef = config.getValue("Boss.ExpCoef");

		this.livingEntity = livingEntity;
		this.health = (int) (livingEntity.getMaxHealth() * healthCoef);
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
	
	public boolean getFound() {
		return found;
	}

	public void setDamageCoef(double damageCoef) {
		this.damageCoef = damageCoef;
	}

	public void setExpCoef(double expCoef) {
		this.expCoef = expCoef;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public void setFound(boolean found) {
		this.found = found;
	}
}

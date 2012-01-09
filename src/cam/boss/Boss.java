package cam.boss;


import org.bukkit.entity.LivingEntity;

import cam.config.LabConfig;

public class Boss {
	
	private double healthCoef = 0;
	private double damageCoef = 0;
	private double expCoef = 0;
	
	private LivingEntity livingEntity = null;
	private int health = 0;
	private boolean found = false;
	private boolean alreadyNotified = false;

	public Boss(LivingEntity livingEntity) {
		healthCoef = LabConfig.BossesData.STATS_HEALTHCOEF.getValue();
		damageCoef = LabConfig.BossesData.STATS_DAMAGECOEF.getValue();
		expCoef = LabConfig.BossesData.STATS_EXPCOEF.getValue();

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
	
	public boolean getAlreadyNotified() {
		return alreadyNotified;
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
	
	public void setAlreadyNotified(boolean alreadyNotified) {
		this.alreadyNotified = alreadyNotified;
	}
}

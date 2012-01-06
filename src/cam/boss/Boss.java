package cam.boss;

import org.bukkit.entity.LivingEntity;

import cam.config.LabConfig;

public class Boss {
	
	private double healthCoef;
	private double damageCoef;
	private double expCoef;
	
	private LivingEntity livingEntity;
	private int health;
	private boolean found = false;

	public Boss(LivingEntity livingEntity) {
		healthCoef = LabConfig.Entry.BOSS_STATS_HEALTHCOEF.getValue();
		damageCoef = LabConfig.Entry.BOSS_STATS_DAMAGECOEF.getValue();
		expCoef = LabConfig.Entry.BOSS_STATS_EXPCOEF.getValue();

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

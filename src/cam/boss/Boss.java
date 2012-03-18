package cam.boss;

import org.bukkit.entity.LivingEntity;

public class Boss {
	
	private LivingEntity livingEntity = null;
	private BossData bossData = null;
	private int health = 0;
	private int lastDamage = 0;
	private boolean found = false;
	private int lastTimeNotified = 0; //For boss proximity
	private int previousTicksLived = 0; //For IsAlive()
	
	public Boss(LivingEntity livingEntity, BossData bossData) {
		this.livingEntity = livingEntity;
		this.bossData = bossData;
		this.health = (int) (livingEntity.getMaxHealth() * bossData.getHealthCoef());
	}
	
	public boolean IsAlive() {
		int currentTicksLived = livingEntity.getTicksLived();
		
		if (currentTicksLived == previousTicksLived)
			return false;
		else {
			previousTicksLived = currentTicksLived;
			return true;
		}
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

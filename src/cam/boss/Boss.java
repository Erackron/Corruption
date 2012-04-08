package cam.boss;

import org.bukkit.entity.LivingEntity;

import cam.player.LabPlayer;

public class Boss {
	
	private LivingEntity livingEntity;
	private BossData bossData;
	private int health;
	private int fireEnchantTick = 0;
	private LabPlayer killer;
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
	
	public int getFireEnchantTick() {
		return fireEnchantTick;
	}
	
	public LabPlayer getKiller() {
		return killer;
	}
	
	public boolean getFound() {
		return found;
	}
	
	public int getLastTimeNotified() {
		return lastTimeNotified;
	}
	
	public void setLivingEntity(LivingEntity livingEntity) {
		this.livingEntity = livingEntity;
	}
	
	public void setBossData(BossData bossData) {
		this.bossData = bossData;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public void setFireEnchantTick(int fireEnchantTick) {
		this.fireEnchantTick = fireEnchantTick;
	}
	
	public void setKiller(LabPlayer killer) {
		this.killer = killer;
	}
	
	public void setFound(boolean found) {
		this.found = found;
	}

	public void setLastTimeNotified(int lastTimeNotified) {
		this.lastTimeNotified = lastTimeNotified;
	}
}

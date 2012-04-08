package cam.boss;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.EntityType;

import cam.drop.Roll;

public class BossData {
	
	private Set<Roll> rolls = new HashSet<Roll>();
	private EntityType entityType;
	private double chance;
	private double chanceFromSpawner;
	private int maxHeight;
	private double healthCoef;
	private double damageCoef;
	private double expCoef;
	
	public BossData(EntityType entityType) {
		this.entityType = entityType;
	}
	
	public void AddRoll(Roll roll) {
		rolls.add(roll);
	}
	
	public Set<Roll> getRolls() {
		return rolls;
	}
	
	public EntityType getEntityType() {
		return entityType;
	}
	
	public double getChance() {
		return chance;
	}
	
	public double getChanceFromSpawner() {
		return chanceFromSpawner;
	}
	
	public int getMaxHeight() {
		return maxHeight;
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
	
	public void setSpawnData(double chance, double chanceFromSpawner, int maxHeight) {
		this.chance = chance;
		this.chanceFromSpawner = chanceFromSpawner;
		this.maxHeight = maxHeight;
	}
	
	public void setStatData(double healthCoef, double damageCoef, double expCoef) {
		this.healthCoef = healthCoef;
		this.damageCoef = damageCoef;
		this.expCoef = expCoef;
	}
}

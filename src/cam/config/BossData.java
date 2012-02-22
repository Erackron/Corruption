package cam.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.CreatureType;

import cam.drop.Roll;

public class BossData {
	
	private List<Roll> rolls = new ArrayList<Roll>();
	private CreatureType creatureType = null;
	private String name = null;
	private double chance = 0.0;
	private double chanceFromSpawner = 0.0;
	private int maxHeight = 0;
	private double healthCoef = 0.0;
	private double damageCoef = 0.0;
	private double expCoef = 0.0;
	
	public BossData(CreatureType creatureType) {
		this.creatureType = creatureType;
	}
	
	public void AddRoll(Roll roll) {
		rolls.add(roll);
	}
	
	public List<Roll> getRolls() {
		return rolls;
	}

	public CreatureType getCreatureType() {
		return creatureType;
	}
	
	public String getName() {
		return name;
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

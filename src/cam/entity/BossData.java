package cam.entity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import cam.ability.Ability;
import cam.drop.Roll;

public class BossData {
	private List<Ability> abilities = new ArrayList<Ability>();
	private List<Roll> rolls = new ArrayList<Roll>();
	private String name;
	private EntityType entityType;
	private ItemStack[] armor;
	private ItemStack weapon;
	private double chance;
	private double chanceFromSpawner;
	private double healthCoef;
	private double damageCoef;
	private double expCoef;
	private double maxSpawnLevel;
	
	public BossData(String name, EntityType entityType) {
		this.name = name;
		this.entityType = entityType;
	}
	
	public void AddAbility(Ability ability) {
		abilities.add(ability);
	}
	
	public void AddRoll(Roll roll) {
		rolls.add(roll);
	}
	
	public List<Ability> getAbilities() {
		return abilities;
	}
	
	public List<Roll> getRolls() {
		return rolls;
	}
	
	public String getName() {
		return name;
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
	
	public double getHealthCoef() {
		return healthCoef;
	}
	
	public double getDamageCoef() {
		return damageCoef;
	}	
	
	public double getExpCoef() {
		return expCoef;
	}
	
	public double getMaxSpawnLevel(){
		return maxSpawnLevel;
	}
	
	public ItemStack[] getArmor(){
		return armor;
	}
	
	public ItemStack getWeapon(){
		return weapon;
	}
	
	public void setSpawnData(double chance, double chanceFromSpawner, double maxSpawnLevel) {
		this.chance = chance;
		this.chanceFromSpawner = chanceFromSpawner;
		this.maxSpawnLevel = maxSpawnLevel;
	}
	
	public void setStatData(double healthCoef, double damageCoef, double expCoef) {
		this.healthCoef = healthCoef;
		this.damageCoef = damageCoef;
		this.expCoef = expCoef;
	}
	
	public void setEquipment(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, int weapon){
		ItemStack[] eq = {boots, leggings, chestplate, helmet};
		armor = eq;
		this.weapon = new ItemStack(weapon);
	}
}

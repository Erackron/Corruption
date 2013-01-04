package com.mcdr.likeaboss.entity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

import com.mcdr.likeaboss.ability.Ability;
import com.mcdr.likeaboss.drop.Roll;


public class BossData {
	private List<Ability> abilities = new ArrayList<Ability>();
	private List<Roll> rolls = new ArrayList<Roll>();
	private String name;
	private EntityType entityType;
	private EquipmentSet bossEquipment = null;
	private List<BossImmunity> immunities;
	private double chance;
	private double chanceFromSpawner;
	private double healthCoef;
	private double damageCoef;
	private double expCoef;
	private double maxSpawnLevel;
	
	public BossData(String name, EntityType entityType) {
		this.name = name;
		this.entityType = entityType;
		immunities = new ArrayList<BossImmunity>();
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
	
	public void setEquipment(EquipmentSet eqS){
		bossEquipment = eqS;
	}
	
	public boolean hasEquipment(){
		return bossEquipment!=null;
	}
	
	public EntityEquipment setRandomEquipment(LivingEntity e){
		return bossEquipment.setRandomEquipment(e);
	}
	
	public void setImmunity(String immunityName, boolean isEnabled){
		for(BossImmunity immunity : BossImmunity.values()){
			if(isEnabled && immunityName.equals(immunity.getNode())){
				immunities.add(immunity);				
			}
		}
	}
	
	public List<BossImmunity> getImmunities(){
		return immunities;
	}
	
	public enum BossImmunity {
		ATTACK_IMMUNE {@Override public String getNode() {return "Attack";}},
		PROJECTILE_IMMUNE {@Override public String getNode() {return "Projectile";}},
		BLOCK_EXPLOSION_IMMUNE {@Override public String getNode() {return "BlockExplosion";}},
		ENTITY_EXPLOSION_IMMUNE {@Override public String getNode() {return "EntityExplosion";}},
		FIRE_IMMUNE {@Override public String getNode() {return "Fire";}},
		LAVA_IMMUNE {@Override public String getNode() {return "Lava";}},
		ENCHANT_FIRETICK_IMMUNE {@Override public String getNode() {return "EnchantFireTick";}},
		ENVIRONMENTAL_FIRETICK_IMMUNE {@Override public String getNode() {return "EnvironmentalFireTick";}},
		FALL_IMMUNE {@Override public String getNode() {return "Fall";}},
		CONTACT_IMMUNE {@Override public String getNode() {return "Contact";}},
		DROWNING_IMMUNE {@Override public String getNode() {return "Drowning";}},
		LIGHTNING_IMMUNE {@Override public String getNode() {return "Lightning";}},
		SUFFOCATION_IMMUNE {@Override public String getNode() {return "Suffocation";}},
		MAGIC_IMMUNE {@Override public String getNode() {return "Magic";}},
		POISON_IMMUNE {@Override public String getNode() {return "Poison";}};
		
		private BossImmunity() {
			
		}
		
		public abstract String getNode();
	}
}

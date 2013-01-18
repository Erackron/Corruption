package com.mcdr.likeaboss.entity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

import com.mcdr.likeaboss.ability.Ability;
import com.mcdr.likeaboss.config.GlobalConfig.BossParam;
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
	private int mcMMOXPBonus;
	private boolean useHealthAsMultiplier;
	private boolean useDamageAsMultiplier;
	private boolean useExperienceAsMultiplier;
	
	public BossData(String name, EntityType entityType) {
		this.name = name;
		this.entityType = entityType;
		setStatsMultipliers(BossParam.USE_HEALTH_AS_MULTIPLIER.getValue(), BossParam.USE_DAMAGE_AS_MULTIPLIER.getValue(), BossParam.USE_EXPERIENCE_AS_MULTIPLIER.getValue());
		setMCMMOXPBonus(BossParam.MCMMO_EXTRA_BOSS_XP.getValue());
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
	
	public int getMCMMOXPBonus(){
		return mcMMOXPBonus;
	}
	
	public boolean useHealthMultiplier(){
		return useHealthAsMultiplier;
	}
	
	public boolean useDamageMultiplier(){
		return useDamageAsMultiplier;
	}
	
	public boolean useExperienceMultiplier(){
		return useExperienceAsMultiplier;
	}
	
	public void setStatsMultipliers(int health, int damage, int experience){
		useHealthAsMultiplier = health==1?true:false;
		useDamageAsMultiplier = damage==1?true:false;
		useExperienceAsMultiplier = experience==1?true:false;
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
	
	public void setMCMMOXPBonus(int bonus){
		mcMMOXPBonus = bonus;
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

package com.mcdr.likeaboss.config;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import com.mcdr.likeaboss.Likeaboss;
import com.mcdr.likeaboss.ability.Ability;
import com.mcdr.likeaboss.drop.Drop;
import com.mcdr.likeaboss.drop.Roll;
import com.mcdr.likeaboss.entity.BossData;
import com.mcdr.likeaboss.entity.BossData.BossImmunity;
import com.mcdr.likeaboss.entity.PigZombieBossData;
import com.mcdr.likeaboss.entity.SkeletonBossData;
import com.mcdr.likeaboss.entity.SlimeBossData;
import com.mcdr.likeaboss.entity.WitherBossData;
import com.mcdr.likeaboss.entity.ZombieBossData;


public class BossConfig extends BaseConfig {
	private static Map<String, BossData> bossesData = new HashMap<String, BossData>();
	private static Set<EntityType> usedBossEntityTypes;
	
	public static void Load() {
		File file = new File(Likeaboss.in.getDataFolder(), "bosses.yml");
		
		if(!file.exists())
			CopyResource(file, "com/mcdr/likeaboss/config/bosses.yml");
		
		LoadBosses(LoadConfig(file));
	}
	
	private static void LoadBosses(YamlConfiguration yamlConfig) {
		Set<String> bossNames = yamlConfig.getKeys(false);
		bossNames.remove("ConfigVersion");
		usedBossEntityTypes = new HashSet<EntityType>();
		for (String bossName : bossNames) {
			ConfigurationSection configurationSection = yamlConfig.getConfigurationSection(bossName);
			
			if (configurationSection == null) {
				Likeaboss.l.warning("[Likeaboss] '" + bossName + "' in bosses config file is invalid.");
				continue;
			}
			
			String entityTypeString = yamlConfig.getString(bossName + ".EntityType");
			EntityType entityType = EntityType.fromName(entityTypeString);
			
			if (entityType == null) {
				Likeaboss.l.warning("[Likeaboss] '" + entityTypeString + "' in bosses config file isn't a valid EntityType.");
				continue;
			}
			
			usedBossEntityTypes.add(entityType);
			
			BossData bossData;
			switch(entityType){
				case ZOMBIE:
					boolean isBaby = yamlConfig.getBoolean(bossName + ".Baby"), isVillager = yamlConfig.getBoolean(bossName + ".Villager");
					bossData = new ZombieBossData(bossName, entityType, isBaby, isVillager);
					break;
				case PIG_ZOMBIE:
					boolean isPigBaby = yamlConfig.getBoolean(bossName + ".Baby"), isAngry = yamlConfig.isSet(bossName + ".Aggressive")?yamlConfig.getBoolean(bossName + ".Aggressive"):true;
					bossData = new PigZombieBossData(bossName, entityType, isPigBaby, isAngry);
					break;
				case SKELETON:
					boolean isWitherSkeleton = yamlConfig.getBoolean(bossName + ".WitherSkeleton");
					bossData = new SkeletonBossData(bossName, entityType, isWitherSkeleton);
					break;
				case WITHER:
					int regenPerSecond = yamlConfig.getInt(bossName + ".HealthRegenPerSecond");
					bossData = new WitherBossData(bossName, entityType, regenPerSecond>0?regenPerSecond:1);
					break;
				case SLIME:
				case MAGMA_CUBE:
					int minsize = yamlConfig.isSet(bossName + ".MinimumSize")?yamlConfig.getInt(bossName + ".MinimalSize"):2;
					int maxsize = yamlConfig.isSet(bossName + ".MaximumSize")?yamlConfig.getInt(bossName + ".MaximalSize"):4;
					bossData = new SlimeBossData(bossName, entityType, minsize, maxsize);
					break;
				default:
					bossData = new BossData(bossName, entityType);
			}

			if (!LoadSpawnValues(bossData, yamlConfig.getConfigurationSection(bossName + ".Spawn"), bossName))
				continue;
			if (!LoadStats(bossData, yamlConfig.getConfigurationSection(bossName + ".Stats"), bossName))
				continue;
			LoadAbilities(bossData, yamlConfig.getStringList(bossName + ".Ability"), bossName);
			LoadLoots(bossData, yamlConfig.getConfigurationSection(bossName + ".Loot"), bossName);
			LoadEquipment(bossData, yamlConfig.getConfigurationSection(bossName));
			LoadImmunities(bossData, yamlConfig.getConfigurationSection(bossName + ".Immunity"), bossName);
			LoadMCMMOXPBonus(bossData, yamlConfig.getInt(bossName + ".mcMMOXPBonus"));
			
			String [] bossNameS = bossName.split("_");
			bossName = bossNameS[0];
			for(int i = 1;i<bossNameS.length;i++){
				bossName += " "+bossNameS[i];
			}
			bossesData.put(bossName, bossData);
		}
	}
	
	private static boolean LoadSpawnValues(BossData bossData, ConfigurationSection spawnSection, String bossName) {
		if (spawnSection == null) {
			Likeaboss.l.warning("[Likeaboss] '" + bossName + ".Spawn' in bosses config file is missing.");
			return false;
		}
		
		if(!(spawnSection.isSet("Probability") && spawnSection.isSet("SpawnerProbability"))){
			Likeaboss.l.warning("[Likaboss] '" + bossName + ".Spawn' in bosses config file is invalid.");
			return false;
		}
		
		double height;
		if(spawnSection.isSet("MaxSpawnHeight"))
			height = spawnSection.getDouble("MaxSpawnHeight");
		else
			height = 256;
	
		bossData.setSpawnData(spawnSection.getDouble("Probability"), spawnSection.getDouble("SpawnerProbability"), height);
		return true;
	}
	
	private static boolean LoadStats(BossData bossData, ConfigurationSection statsSection, String bossName) {
		
		if(!(statsSection.isSet("Health") && statsSection.isSet("Damage") && statsSection.isSet("Experience"))){
			Likeaboss.l.info("[Likeaboss] Missing values in '" + bossName + ".Stats'");
			return false;
		}
		
		bossData.setStatData(statsSection.getDouble("Health"), statsSection.getDouble("Damage"), statsSection.getDouble("Experience"));
		return true;
	}
	
	private static void LoadAbilities(BossData bossData, List<String> abilityNames, String bossName) {
		Map<String, Ability> abilities = AbilityConfig.getAbilities();
		
		for (String abilityName : abilityNames) {
			if (!abilities.containsKey(abilityName)) {
				Likeaboss.l.warning("[Likeaboss] '" + bossName + ".Ability." + abilityName + "' in bosses config file isn't a valid ability.");
				continue;
			}
			
			bossData.AddAbility(abilities.get(abilityName));
		}
	}
	
	private static void LoadLoots(BossData bossData, ConfigurationSection lootSection, String bossName) {
		if (lootSection == null) {
			Likeaboss.l.warning("[Likeaboss] '" + bossName + ".Loot" + "' in bosses config file is invalid.");
			return;
		}
		
		Set<String> rollStrings = lootSection.getKeys(false);
		
		for (String rollString : rollStrings) {
			ConfigurationSection rollSection = lootSection.getConfigurationSection(rollString);
			
			if (rollSection == null) {
				Likeaboss.l.warning("[Likeaboss] '" + bossName + ".Loot." + rollString + "' in bosses config file is invalid.");
				continue;
			}
			
			Roll roll = new Roll();
			Map<String, Object> drops = rollSection.getValues(false);
			
			for (Entry<String, Object> dropEntry : drops.entrySet()) {
				String dropString = dropEntry.getValue().toString();
				
//				if (!IsValidString(rawValue)) {
//					Likeaboss.logger.warning("[Likeaboss] Invalid values for '" + dropEntry + "' in '" + world.getName() + "' config file");
//					continue;
//				}
				
				String[] dropValues = dropString.split(" ");
				
				if (dropValues.length < 4) {
					Likeaboss.l.warning("[Likeaboss] Missing values for '" + bossName + ".Loot." + rollString + "." + dropEntry.getKey() + "' in bosses config file.");
					continue;
				}
				
				Material material = null;
				short metaData = 0;
				
				if (dropValues[0].contains(":")) {
					String[] tempData = dropValues[0].split(":");
					material = Material.getMaterial(Integer.valueOf(tempData[0]));
					metaData = Short.valueOf(tempData[1]);
				}
				else
					material = Material.getMaterial(Integer.valueOf(dropValues[0]));
				
				Drop drop = new Drop(material, metaData, Double.valueOf(dropValues[1]), Integer.valueOf(dropValues[2]), Integer.valueOf(dropValues[3]));
				
				roll.AddDrop(drop);
			}
			
			bossData.AddRoll(roll);
		}
	}
	
	public static void LoadEquipment(BossData bossData, ConfigurationSection section){
		String equipmentSetName = section.getString("EquipmentSet");
		if(equipmentSetName == null)
			return;
		if(EquipmentConfig.equipmentSets.containsKey(equipmentSetName))
			bossData.setEquipment(EquipmentConfig.equipmentSets.get(equipmentSetName));
		else
			Likeaboss.l.warning("[Likeaboss] '" + section.getName() + ".EquipmentSet' in bosses config file is invalid or doesn't exist.");
	}
	
	public static void LoadImmunities(BossData bossData, ConfigurationSection section, String bossName){
		if(section == null){
			Likeaboss.l.warning("[Likeaboss] '" + bossName + ".Immunities" + "' in bosses config file doesn't exist.");
			return;
		}
		
		for(BossImmunity immunities : BossImmunity.values()){
			if(immunities.getNode() != null){
				boolean immunityNode;
				if(section.isSet(immunities.getNode()))
					immunityNode = section.getBoolean(immunities.getNode());
				else
					immunityNode = false;
				bossData.setImmunity(immunities.getNode(), immunityNode);
			}			
		}
		
	}
	
	public static void LoadMCMMOXPBonus(BossData bossData, int xp){
		bossData.setMCMMOXPBonus(xp);
	}
	
	public static Map<String, BossData> getBossesData() {
		return bossesData;
	}
	
	public static Set<EntityType> getEntityTypesUsed(){
		return usedBossEntityTypes;
	}	
}


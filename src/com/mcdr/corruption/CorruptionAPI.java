package com.mcdr.corruption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.mcdr.corruption.config.BossConfig;
import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.player.CorPlayer;
import com.mcdr.corruption.player.CorPlayerManager;
import com.mcdr.corruption.util.Utility;


public class CorruptionAPI {
	
	/**
	 * Get a map of available EntityTypes and the bossnames that are available for it.
	 * @return the map containing all the available bossnames grouped by EntityType
	 * @since Corruption 2.2
	 */
	public static Map<EntityType, List<String>> getBossNames(){
		Map<EntityType, List<String>> result = new HashMap<EntityType, List<String>>();
		
		for(EntityType entityType: BossConfig.getEntityTypesUsed()){
			result.put(entityType, new ArrayList<String>());
		}

		for(Entry<String, BossData> entry: BossConfig.getBossesData().entrySet()){
			result.get(entry.getValue().getEntityType()).add(entry.getKey());
		}
		
		return result;
	}
	
	/**
	 * Get a random bossName belonging to a certain entityType
	 * @param entityType The EntityType of the bossName you want
	 * @return a random bossName that has the supplied EntityType or null if no bossName with this EntityType exists
	 * @since Corruption 2.2
	 */
	public static String getRandomBossName(EntityType entityType){
		List<String> bossNames = getBossNames().get(entityType);
		if(bossNames==null)
			return null;
		return bossNames.get(Utility.Random(0, bossNames.size()-1));
	}
	
	/**
	 * Make the supplied livingEntity a (random) boss of the appropriate entityType
	 * @param livingEntity The LivingEntity to make a boss of
	 * @return The boss object of the new boss or null if no boss with the appropriate EntityType has been defined or the LivingEntity was invalid
	 * @since Corruption 2.2
	 */
	public static Boss addBoss(LivingEntity livingEntity){
		return addBoss(livingEntity, getRandomBossName(livingEntity.getType()));
	}
	
	/**
	 * Make the supplied livingEntity a (random) boss of the appropriate entityType
	 * @param livingEntity The LivingEntity to make a boss of
	 * @param updateEntity Whether or not to adjust the entity with params from the BossData to force a WitherSkeleton for example
	 * @return The boss object of the new boss or null if no boss with the appropriate EntityType has been defined or the LivingEntity was invalid
	 * @since Corruption 2.2
	 */
	public static Boss addBoss(LivingEntity livingEntity, boolean updateEntity){
		return addBoss(livingEntity, getRandomBossName(livingEntity.getType()), updateEntity);
	}
	
	/**
	 * Make the supplied livingEntity a boss of the bossName type
	 * @param livingEntity  The LivingEntity to make a boss of
	 * @param bossName The name of the boss type this livingEntity will be one of
	 * @return The boss object of the new boss or null if no boss with the appropriate EntityType has been defined or the LivingEntity was invalid
	 * @since Corruption 2.2
	 */
	public static Boss addBoss(LivingEntity livingEntity, String bossName){
		return addBoss(livingEntity, bossName, true);
	}
	
	/**
	 * Make the supplied livingEntity a boss of the bossName type
	 * @param livingEntity  The LivingEntity to make a boss of
	 * @param bossName The name of the boss type this livingEntity will be one of
	 * @param updateEntity Whether or not to adjust the entity with params from the BossData to force a WitherSkeleton for example
	 * @return The boss object of the new boss or null if no boss with the appropriate EntityType has been defined or the LivingEntity was invalid
	 * @since Corruption 2.2
	 */
	public static Boss addBoss(LivingEntity livingEntity, String bossName, boolean updateEntity){
		if(bossName==null||livingEntity==null||!livingEntity.isValid())
			return null;
		BossData bossData = BossConfig.getBossesData().get(bossName);
		if(bossData==null||livingEntity.getType()!=bossData.getEntityType())
			return null;
		if(updateEntity)
			CorEntityManager.adjustSpecificEntities(livingEntity, bossData, livingEntity.getType());
		Boss boss = new Boss(livingEntity, bossData);
		CorEntityManager.addBoss(boss);
		return boss;
	}
	
	/**
	 * Spawn a corrupted at a location using an EntityType.
	 * @param loc The location to spawn the boss at
	 * @param entityType The EntityType of the new bosses
	 * @return The spawned boss or null if the bossName is invalid
	 * @since Corruption 2.2
	 */
	public static Boss spawnBoss(Location loc, EntityType entityType){
		return spawnBoss(loc, getRandomBossName(entityType));
	}
	
	/**
	 * Spawn a corrupted at a location using a bossname.
	 * To see which names you can use see the {@link #getBossNames()}
	 * @param loc The location to spawn the boss at
	 * @param bossName The name of the boss
	 * @return The spawned boss or null if the bossName is invalid
	 * @since Corruption 2.2
	 */
	public static Boss spawnBoss(Location loc, String bossName){
		List<Boss> entities = spawnBoss(loc, bossName, 1);
		if(entities==null)
			return null;
		return entities.get(0);
	}
	
	/**
	 * Spawn a corrupted at a location using an entityType.
	 * @param loc The location to spawn the bosses at
	 * @param entityType The EntityType of the new bosses
	 * @param amount The amount of bosses to spawn
	 * @return A List of the spawned bosses or null if the bossName is invalid or no bosses were spawned
	 * @since Corruption 2.2
	 */
	public static List<Boss> spawnBoss(Location loc, EntityType entityType, int amount){
		return spawnBoss(loc, getRandomBossName(entityType), amount);
	}
	
	/**
	 * Spawn a certain amount of corrupted at a location using a bossname.
	 * To see which names you can use see the {@link #getBossNames()}
	 * @param loc The location to spawn the bosses at
	 * @param bossName The name of the boss
	 * @param amount The amount of bosses to spawn
	 * @return A List of the spawned bosses or null if the bossName is invalid or no bosses were spawned
	 * @since Corruption 2.2
	 */
	public static List<Boss> spawnBoss(Location loc, String bossName, int amount){
		if(amount<1)
			amount = 1;
		if(loc==null||bossName==null)
			return null;
		
		List<Boss> entities = new ArrayList<Boss>();
		BossData bossData = null;
		for (Entry<String, BossData> bossesDataEntry : BossConfig.getBossesData().entrySet()) {
			if (!bossesDataEntry.getKey().equalsIgnoreCase(bossName))
				continue;
			bossData = bossesDataEntry.getValue();
		}
		
		if(bossData==null)
			return null;
		
		Boss spawnedBoss;
		for (int i = 0 ; i < amount ; i++) {
			spawnedBoss = CorEntityManager.spawnBossEntity(loc, bossData.getEntityType(), bossData);
			if(spawnedBoss!=null)
				entities.add(spawnedBoss);
		}
		
		if(entities.size()<1)
			return null;
		
		return entities;
	}
	
	/**
	 * @param p The player to get the CorPlayer object of
	 * @return CorPlayer the CorPlayer object belonging to the player
	 */
	public static CorPlayer getCorPlayer(Player p){
		CorPlayer corPlayer = CorPlayerManager.getCorPlayer(p);	
		return corPlayer!=null?corPlayer:CorPlayerManager.addCorPlayer(p);
	}
	
	/**
	 * @param e Entity to get the boss object of
	 * @return Boss the Boss object belonging to the entity or null if the entity is not a boss
	 */
	public static Boss getBoss(Entity e){
		return CorEntityManager.getBoss(e);
	}
	
	/**
	 * @param e The entity that you want to check
	 * @return boolean whether the entity is a boss or not
	 */
	public static boolean isBoss(Entity e){
		return (getBoss(e)!=null);
	}
	
	/**
	 * @param e Entity to get the health of
	 * @return int The health of the boss or -1 if the entity is not a boss
	 */
	public static double getHealth(Entity e){
		if(isBoss(e))
			return getBoss(e).getHealth();
		return -1;
	}
	
	/**	 
	 * @param e Entity to get the health coefficient of
	 * @see Use {@link #isUsingAbsoluteHealth(Entity e)} to check if this value is a multiplier or an absolute value
	 * @return double The health coefficient of the boss or -1.0 if the entity is not a boss
	 */
	public static double getHealthCoef(Entity e){
		if(isBoss(e))
			return getBoss(e).getBossData().getHealthCoef();
		return -1.0;
	}
	
	/** 	 
	 * @param e Entity to get to check if having an absolute health
	 * @return True if this entity has an absolute health, false if it doesn't or the entity is not a boss 
	 */
	public static boolean isUsingAbsoluteHealth(Entity e){
        return isBoss(e) && !getBoss(e).getBossData().useHealthMultiplier();
    }
	
	/**
	 * @deprecated Using livingEntity.getMaxHealth() is usually sufficient. This method was put in place when livingEntity.setMaxHealth() was not yet available.
	 * @param e Entity to get the max health of
	 * @return int The maximum health of the boss or entity. Will return 0 if the entity is not alive anymore
	 */
	public static double getMaxHealth(Entity e){
		if(e instanceof LivingEntity){
			if(isBoss(e))
				return getBoss(e).getMaxHealth();
			return (int)((LivingEntity)e).getMaxHealth();
		} else
			return 0;
	}
	
	/**
	 * @param e Entity of the Boss you want to get the name of
	 * @return String The name of the boss, or an empty string when the entity is not a boss.
	 */
	public static String getName(Entity e){
		if(isBoss(e))
				return Utility.parseMessage("{BOSSNAME}", getBoss(e));
		return "";
	}
	
	/**
	 * @param e Entity of the Boss you want to get the raw/unformatted name of
	 * @return String The raw/unformatted name of the boss, or an empty string when the entity is not a boss.
	 */
	public static String getRawName(Entity e){
		if(isBoss(e))
				return getBoss(e).getRawName();
		return "";
	}
	
	/**
	 * @param e Entity to set the health of
	 * @param health the amount of health you want the boss to have
	 */
	public static void setHealth(Entity e, int health){
		if(isBoss(e))
			getBoss(e).setHealth(health);
	}
	
	
	/**
	 * @param e The entity that you want to check
	 * @return boolean Whether the Boss is dead, when the entity is not a boss, it will return true
	 */
	public static boolean isDead(Entity e) {
		if(isBoss(e))
			isDead(getBoss(e));
		return true;
	}
	
	/**
	 * @param b The boss that you want to check
	 * @return boolean Whether the Boss is dead
	 */
	public static boolean isDead(Boss b){
		return CorEntityManager.isDead(b);
	}

	/**
	 * @return ArrayList<Entity> an arraylist of all the entities that belong to a boss
	 */
	public static ArrayList<Entity> getBossEntities() {
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for(Boss b: CorEntityManager.getBosses()){
			entities.add(b.getLivingEntity());
		}
		return entities;
	}
	
	/**
	 * @param first The first location
	 * @param second The second location
	 * @param minDistance The minimum distance between the locations
	 * @param maxDistance The maximum distance between the locations
	 * @return boolean Whether or not the two locations match the minDistance and maxDistance
	 */
	public static boolean isNear(Location first, Location second, int minDistance, int maxDistance) {
		return Utility.isNear(first, second, minDistance, maxDistance);
	}
	
	/**
	 * @param e Entity to check the metadatatag isBoss of
	 * @return boolean Whether the entity has the isBoss metadatatag and whether or not it received it from Corruption
	 */
	public static boolean hasBossMetatag(Entity e){
		return getBossMetatag(e)!=null;
	}
	
	/**
	 * @param e Entity to get the bossname metadatatag isBoss of
	 * @return String the bossName of this entity belonging to its metadatatag or null if it doesn't have the isBoss tag
	 */
	public static String getBossMetatag(Entity e){
		String key = "isBoss";
		
		if(!e.hasMetadata(key))
			return null;

		List<MetadataValue> list = e.getMetadata(key);

        for (MetadataValue metaV : list) {
            if (!(metaV instanceof FixedMetadataValue))
                continue;
            else if (!metaV.getOwningPlugin().equals(Corruption.in))
                continue;          
            return (String) metaV.value();
        }

        return null;
	}
}

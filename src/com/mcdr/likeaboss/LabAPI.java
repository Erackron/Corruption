package com.mcdr.likeaboss;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.mcdr.likeaboss.entity.Boss;
import com.mcdr.likeaboss.entity.LabEntityManager;
import com.mcdr.likeaboss.player.LabPlayer;
import com.mcdr.likeaboss.player.LabPlayerManager;
import com.mcdr.likeaboss.util.Utility;


public class LabAPI {
		
	/**
	 * @param p The player to get the LabPlayer object of
	 * @return LabPlayer the LabPlayer object belonging to the player
	 */
	public static LabPlayer getLabPlayer(Player p){
		LabPlayer labPlayer = LabPlayerManager.getLabPlayer(p);	
		return labPlayer!=null?labPlayer:LabPlayerManager.AddLabPlayer(p);
	}
	
	/**
	 * @param e Entity to get the boss object of
	 * @return Boss the Boss object belonging to the entity or null if the entity is not a boss
	 */
	public static Boss getBoss(Entity e){
		return LabEntityManager.getBoss(e);
	}
	
	/**
	 * @param e The entity that you want to check
	 * @return boolean whether the entity is a boss or not
	 */
	public static boolean isBoss(Entity e){
		return (getBoss(e)!=null||hasBossMetatag(e));
	}
	
	/**
	 * @param e Entity to get the health of
	 * @return int The health of the boss or -1 if the entity is not a boss
	 */
	public static int getHealth(Entity e){
		if(isBoss(e))
			return getBoss(e).getHealth();
		return -1;
	}
	
	/**
	 * @param e Entity to get the health coefficient of
	 * @return double The health coefficient of the boss or -1.0 if the entity is not a boss
	 */
	public static double getHealthCoef(Entity e){
		if(isBoss(e))
			return getBoss(e).getBossData().getHealthCoef();
		return -1.0;
	}
	
	/**
	 * @param e Entity to get the max health of
	 * @return int The maximum health of the boss or entity. Will return 0 if the entity is not alive anymore
	 */
	public static int getMaxHealth(Entity e){
		if(e instanceof LivingEntity){
			if(isBoss(e))
				return (int) ((LivingEntity)e).getMaxHealth();
			return ((LivingEntity)e).getMaxHealth();
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
				return getBoss(e).getBossData().getName();
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
		return LabEntityManager.IsDead(b);
	}

	/**
	 * @return ArrayList<Entity> an arraylist of all the entities that belong to a boss
	 */
	public static ArrayList<Entity> getBossEntities() {
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for(Boss b: LabEntityManager.getBosses()){
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
	 * @return boolean Whether the entity has the isBoss metadatatag and whether or not it received it from Likeaboss
	 */
	public static boolean hasBossMetatag(Entity e){
		String key = "isBoss";
		if(!e.hasMetadata(key))
			return false;

		LazyMetadataValue meta = new FixedMetadataValue(Likeaboss.in, true);
		List<MetadataValue> list = e.getMetadata(key);
		if (list.contains(meta))
            return true;

        for (MetadataValue metaV : list) {
            if (!(metaV instanceof FixedMetadataValue))
                continue;
            else if (!metaV.getOwningPlugin().equals(Likeaboss.in))
                continue;
            else if (!metaV.value().equals(meta.value()))
                continue;
            
            return true;
        }

        return false;
	}
	
}

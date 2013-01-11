package com.mcdr.likeaboss.command;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;

import com.mcdr.likeaboss.config.BossConfig;
import com.mcdr.likeaboss.config.GlobalConfig.CommandParam;
import com.mcdr.likeaboss.entity.Boss;
import com.mcdr.likeaboss.entity.BossData;
import com.mcdr.likeaboss.entity.LabEntityManager;
import com.mcdr.likeaboss.entity.ZombieBossData;
import com.mcdr.likeaboss.entity.SkeletonBossData;


public abstract class SpawnCommand extends BaseCommand {
	public static void Process() {
		if (!CheckPermission("lab.spawn", false))
			return;
		
		Map<String, BossData> bossesData = BossConfig.getBossesData();
		
		if (args.length < 2) {
			SendUsage(bossesData);
			return;
		}
		
		for (Entry<String, BossData> bossesDataEntry : bossesData.entrySet()) {
			if (!bossesDataEntry.getKey().equalsIgnoreCase(args[1]))
				continue;
			
			int amount = 1;
			
			if (args.length >= 3) {
				try {
					amount = Integer.parseInt(args[2]);
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.GRAY + args[2] + ChatColor.WHITE + " isn't an integer.");
					return;
				}
				
				int max = CommandParam.SPAWN_MAX.getValue();
				if (amount > max) {
					sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "You are not allowed to spawn more than " + ChatColor.GRAY + max + ChatColor.WHITE + " boss(es) at a time.");
					return;
				}
			}
			
			Spawn(bossesDataEntry.getValue(), amount);
			return;
		}
		
		//Invalid creature name
		SendUsage(bossesData);
	}
	
	private static Boolean Spawn(BossData bossData, int amount) {
		Player player = (Player) sender;
		Location location = player.getTargetBlock(null, 100).getRelative(BlockFace.UP).getLocation();
		World world = player.getWorld();
		EntityType entityType = bossData.getEntityType();
		LivingEntity spawnedCreature;
		
		for (int i = 0 ; i < amount ; i++) {
			Entity spawnedEntity = world.spawnEntity(location, entityType);
			if (spawnedEntity.isValid())
				spawnedCreature = (LivingEntity) spawnedEntity;
			else
				return false;
			
			//Slimes are always big
			if (Slime.class.isAssignableFrom(entityType.getEntityClass())) {
				Slime slime = (Slime) spawnedCreature;
				slime.setSize(4);
			}
			
			//Check and set if it has to be a baby or villager zombie
			if (Zombie.class.isAssignableFrom(entityType.getEntityClass())) {
				Zombie zombie = (Zombie) spawnedCreature;
				zombie.setBaby(((ZombieBossData) bossData).isBaby());
				zombie.setVillager(((ZombieBossData) bossData).isVillager());
			}
			
			//Check and set if it has to be a normal or wither skeleton
			if (Skeleton.class.isAssignableFrom(entityType.getEntityClass())) {
				Skeleton skeleton = (Skeleton) spawnedCreature;
				skeleton.setSkeletonType(((SkeletonBossData) bossData).getSkeletonType());
			}
			
			Boss boss = new Boss(spawnedCreature, bossData);
			
			LabEntityManager.AddBoss(boss);
		}
		return true;
	}
	
	private static void SendUsage(Map<String, BossData> bossesData) {
		StringBuilder bossListBuilder = new StringBuilder();
		
		for (String key : bossesData.keySet()) {
			bossListBuilder.append(key + ", ");
		}
		
		bossListBuilder.substring(0, bossListBuilder.length() - 2);
		
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Allowed Creatures:");
		sender.sendMessage(ChatColor.GRAY + bossListBuilder.toString());
	}
}

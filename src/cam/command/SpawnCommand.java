package cam.command;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;

import cam.Likeaboss;
import cam.boss.Boss;
import cam.boss.BossData;
import cam.config.GlobalConfig.CommandParam;

public abstract class SpawnCommand extends BaseCommand {
	
	public static void Process() {
		if (!CheckPermission("lab.spawn", false))
			return;
		
		Set<EntityType> spawnableEntities = new HashSet<EntityType>();
		for (EntityType entityType : EntityType.values()) {
			if (entityType == EntityType.UNKNOWN)
				continue;
			
			Class<? extends Entity> entityClass = entityType.getEntityClass();
			if (Monster.class.isAssignableFrom(entityClass) && entityClass != Monster.class ||
					Slime.class.isAssignableFrom(entityClass) ||
					Ghast.class.isAssignableFrom(entityClass))
				spawnableEntities.add(entityType);
		}
		
		if (args.length < 2) {
			SendUsage(spawnableEntities);
			return;
		}
		
		for (EntityType entityType : spawnableEntities) {
			if (!entityType.getName().equalsIgnoreCase(args[1]))
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
			
			Spawn(entityType.getName(), amount);
			return;
		}
		
		//Invalid creature name
		SendUsage(spawnableEntities);
	}
	
	private static void Spawn(String creatureName, int amount) {
		Player player = (Player) sender;
		World world = player.getWorld();
		Block block = player.getTargetBlock(null, 100).getRelative(BlockFace.UP);
		EntityType entityType = EntityType.fromName(creatureName);
		
		for (int i = 0 ; i < amount ; i++) {
			LivingEntity spawnedCreature = world.spawnCreature(block.getLocation(), entityType);
			
			if (Slime.class.isAssignableFrom(entityType.getEntityClass())) {
				Slime slime = (Slime) spawnedCreature;
				slime.setSize(4);
			}
			
			BossData bossData = Likeaboss.instance.getLabConfig().getWorldConfig(world).getBossData(entityType);
			
			if (bossData == null)
				sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Nothing in the config file for " + ChatColor.GRAY + creatureName + ChatColor.WHITE + ".");
			else {
				Boss boss = new Boss(spawnedCreature, bossData);
				Likeaboss.instance.getBossManager().AddBoss(boss);
			}
		}
	}
	
	private static void SendUsage(Set<EntityType> spawnableEntities) {
		String creatureList = "";
		for (EntityType entityType : spawnableEntities)
			creatureList += entityType.getName() + ", ";
		creatureList = creatureList.substring(0, creatureList.length() - 2);
		
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Allowed Creatures:");
		sender.sendMessage(ChatColor.GRAY + creatureList);
	}
}

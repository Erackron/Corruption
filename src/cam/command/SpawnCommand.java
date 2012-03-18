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

import cam.boss.BossData;
import cam.boss.BossManager;

public abstract class SpawnCommand extends CommandBase {
	
	private static Set<EntityType> spawnableEntities = new HashSet<EntityType>();
	
	public static void Process() {
		if (!CheckPermission("lab.spawn", false))
			return;
		
		if (spawnableEntities.isEmpty()) {
			for (EntityType entityType : EntityType.values()) {
				Class<? extends Entity> entityClass = entityType.getEntityClass();
				
				if (entityClass == null)
					continue;
				
				if (Monster.class.isAssignableFrom(entityClass) && entityClass != Monster.class ||
						Slime.class.isAssignableFrom(entityClass) ||
						Ghast.class.isAssignableFrom(entityClass))
					spawnableEntities.add(entityType);
			}
		}
		
		boolean spawn = false;
		String creatureName = null;
		
		if (args.length >= 2) {
			for (EntityType entityType : spawnableEntities) {
				if (entityType.getName().equalsIgnoreCase(args[1])) {
					spawn = true;
					creatureName = entityType.getName();
					break;
				}
			}
		}
		
		if (!spawn) {
			String creatureList = "";
			
			for (EntityType entityType : spawnableEntities)
				creatureList += entityType.getName() + ", ";
			creatureList = creatureList.substring(0, creatureList.length() - 2);
			
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Allowed Creatures");
			sender.sendMessage(ChatColor.GRAY + creatureList);
		}
		
		else {
			BossManager bossManager = plugin.getBossManager();
			
			Player player = (Player) sender;
			World world = player.getWorld();
			Block block = player.getTargetBlock(null, 100).getRelative(BlockFace.UP);
			
			EntityType entityType = EntityType.fromName(creatureName);
			LivingEntity spawnedCreature = world.spawnCreature(block.getLocation(), entityType);
			
			if (Slime.class.isAssignableFrom(entityType.getEntityClass())) {
				Slime slime = (Slime) spawnedCreature;
				slime.setSize(4);
			}
			
			BossData bossData = plugin.getLabConfig().getWorldConfig(world).getBossData(entityType);
			
			if (bossData == null)
				sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Nothing in the config file for " + ChatColor.GRAY + creatureName + ChatColor.WHITE + ".");
			else
				bossManager.AddBoss(spawnedCreature, bossData);
		}
	}
}

package cam.command;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;

import cam.boss.BossManager;

public abstract class SpawnCommand extends CommandBase {
	
	private static Set<String> spawnableCreatureNames = new HashSet<String>();
	private static CreatureType[] creatureTypes = CreatureType.values();
	
	public static boolean Process() {
		if (spawnableCreatureNames.isEmpty()) {
			for (CreatureType creatureType : creatureTypes) {
				Class<? extends Entity> entityClass = creatureType.getEntityClass();
				
				if (Monster.class.isAssignableFrom(entityClass) && entityClass != Monster.class ||
					Slime.class.isAssignableFrom(entityClass) ||
					Ghast.class.isAssignableFrom(entityClass))
					spawnableCreatureNames.add(creatureType.getName());
			}
		}
		
		boolean spawn = false;
		String creatureName = null;
		
		if (args.length >= 2) {
			for (String name : spawnableCreatureNames) {
				if (name.equalsIgnoreCase(args[1])) {
					spawn = true;
					creatureName = name;
					break;
				}
			}
		}
		
		if (!spawn) {
			String creatureList = "";
			
			for (String name : spawnableCreatureNames)
				creatureList += name + ", ";
			creatureList = creatureList.substring(0, creatureList.length() - 2);
			
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Allowed Creatures");
			sender.sendMessage(ChatColor.GRAY + creatureList);
		}
		
		else {
			BossManager bossManager = plugin.getBossManager();
			Player player = (Player) sender;
			World world = player.getWorld();
			Block block = player.getTargetBlock(null, 100).getRelative(BlockFace.UP);
			CreatureType creatureType = CreatureType.fromName(creatureName);
			
			LivingEntity spawnedCreature = world.spawnCreature(block.getLocation(), creatureType);
			
			if (Slime.class.isAssignableFrom(creatureType.getEntityClass())) {
				Slime slime = (Slime) spawnedCreature;
				
				slime.setSize(4);
				slime.setHealth(slime.getMaxHealth());
			}
			
			bossManager.AddBoss(spawnedCreature);
		}
		
		return true;
	}
}

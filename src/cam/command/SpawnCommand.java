package cam.command;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import cam.Likeaboss;
import cam.boss.BossManager;

public class SpawnCommand extends CommandBase {
	
	private static String creatureList = "";
	private static CreatureType[] creatureTypes = CreatureType.values();
	
	public SpawnCommand(Likeaboss plugin) {
		super(plugin);
	}

	public static boolean Process(String[] args) {
		if (creatureList.isEmpty()) {
			for (CreatureType creatureType : creatureTypes) {
				if (Monster.class.isAssignableFrom(creatureType.getEntityClass()))
					creatureList += creatureType.getName() + ", ";
			}
			
			creatureList = creatureList.substring(0, creatureList.length() - 2);
		}
		
		boolean spawn = false;
		String creatureName = null;
		
		if (args.length >= 2) {
			creatureName = GetGoodName(args[1]);
			
			if (creatureName != null)
				spawn = true;
		}
		
		if (!spawn) {
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Allowed Creatures");
			sender.sendMessage(ChatColor.GRAY + creatureList);
			
			return true;
		}
		
		Player player = (Player) sender;
		World world = player.getWorld();
		Block block = player.getTargetBlock(null, 100);
		CreatureType creatureType = CreatureType.fromName(creatureName);
		LivingEntity spawnedCreature = world.spawnCreature(block.getLocation(), creatureType);
		BossManager bossManager = plugin.getBossManager();
		bossManager.AddBoss(spawnedCreature);
		
		return true;
	}
	
	private static String GetGoodName(String name) {
		for (CreatureType creatureType : creatureTypes) {
			if (Monster.class.isAssignableFrom(creatureType.getEntityClass()))
				if (creatureType.getName().equalsIgnoreCase(name))
					return creatureType.getName();
		}
		
		return null;
	}
}

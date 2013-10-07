package com.mcdr.corruption.command;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.BossConfig;
import com.mcdr.corruption.config.GlobalConfig.CommandParam;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.entity.data.BossData;


public abstract class SpawnCommand extends BaseCommand {
	public static void process() {
		if (!checkPermission("cor.spawn", false))
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
					amount = Math.abs(Integer.parseInt(args[2]));
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.GOLD + "["+Corruption.pluginName+"] " + ChatColor.GRAY + args[2] + ChatColor.WHITE + " isn't an integer.");
					return;
				}
				
				int max = CommandParam.SPAWN_MAX.getValue();
				if (amount > max) {
					sender.sendMessage(ChatColor.GOLD + "["+Corruption.pluginName+"] " + ChatColor.WHITE + "You are not allowed to spawn more than " + ChatColor.GRAY + max + ChatColor.WHITE + " boss(es) at a time.");
					return;
				}
			}
			
			Spawn(bossesDataEntry.getValue(), amount);
			return;
		}
		
		//Invalid creature name
		SendUsage(bossesData);
	}
	
	@SuppressWarnings("deprecation")
	private static Boolean Spawn(BossData bossData, int amount) {
		Player player = (Player) sender;
		Location location = player.getTargetBlock(null, 100).getRelative(BlockFace.UP).getLocation(); //TODO Find alternative for LivingEntity.getTargetBlock(HashSet<Byte> transparent, int maxDistance)
		location.setWorld(player.getWorld());
		EntityType entityType = bossData.getEntityType();
		
		//Withers should not be spawned using a command.
		if (Wither.class.isAssignableFrom(entityType.getEntityClass())) {
			sender.sendMessage(ChatColor.GOLD + "["+Corruption.pluginName+"] " + ChatColor.RED + "Withers should not be spawned using a command.\nDoing so will result in unexpected behaviour.");
			return false;
		}
		
		for (int i = 0 ; i < amount ; i++) {
			if(CorEntityManager.spawnBossEntity(location, entityType, bossData)==null)
				return false;
		}
		
		return true;
	}
	
	
	
	private static void SendUsage(Map<String, BossData> bossesData) {
		StringBuilder bossListBuilder = new StringBuilder();
		
		for (String key : bossesData.keySet()) {
			if(bossesData.get(key).getEntityType()==EntityType.WITHER)
				bossListBuilder.append(ChatColor.STRIKETHROUGH + key + ChatColor.RESET + ChatColor.GRAY + ", ");
			else
				bossListBuilder.append(key + ", ");
		}
		
		bossListBuilder.substring(0, bossListBuilder.length() - 2);
		
		sender.sendMessage(ChatColor.GOLD + "["+Corruption.pluginName+"] " + ChatColor.WHITE + "Available Bosses:");
		sender.sendMessage(ChatColor.GRAY + bossListBuilder.toString());
	}
}

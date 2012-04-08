package cam.command;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import cam.Likeaboss;
import cam.boss.BossManager;
import cam.stats.Stats;

public abstract class InfoCommand extends BaseCommand {
	
	public static void Process() {
		if (!CheckPermission("lab.info", true))
			return;
		
		Stats stats = Likeaboss.instance.getStats();
		BossManager bossManager = Likeaboss.instance.getBossManager();
		
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Info");
		sender.sendMessage(ChatColor.GRAY + "Boss Killed: " + stats.getTotalBossesKilled());
		sender.sendMessage(ChatColor.GRAY + "Boss Count: " + bossManager.getBosses().size());
			
		Map<Material, Integer> droped = stats.getDroped();
		for (Entry<Material, Integer> entry : droped.entrySet())
			sender.sendMessage(ChatColor.GRAY + entry.getKey().toString() + " found: " + entry.getValue());
	}
}

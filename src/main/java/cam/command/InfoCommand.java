package cam.command;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import cam.entity.LabEntityManager;
import cam.stats.StatsManager;

public abstract class InfoCommand extends BaseCommand {
	public static void Process() {
		if (!CheckPermission("lab.info", true))
			return;
		
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Info");
		sender.sendMessage(ChatColor.GRAY + "Boss Killed: " + StatsManager.getBossesKilled());
		sender.sendMessage(ChatColor.GRAY + "Boss Count: " + LabEntityManager.getBosses().size());
			
		Map<Material, Integer> droped = StatsManager.getDroped();
		for (Entry<Material, Integer> entry : droped.entrySet())
			sender.sendMessage(ChatColor.GRAY + entry.getKey().toString() + " found: " + entry.getValue());
	}
}

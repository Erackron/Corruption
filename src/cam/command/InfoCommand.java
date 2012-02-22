package cam.command;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import cam.boss.BossManager;
import cam.drop.DropCalculator;

public abstract class InfoCommand extends CommandBase {

	public static boolean Process() {
		BossManager bossManager = plugin.getBossManager();
		DropCalculator dropCalculator = plugin.getDropCalculator();
		
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Info");
		sender.sendMessage(ChatColor.GRAY + "Boss Killed: " + bossManager.getBossKilled());
		sender.sendMessage(ChatColor.GRAY + "Boss Count: " + bossManager.getBosses().size());
			
		Map<Material, Integer> droped = dropCalculator.getDroped();
		for (Entry<Material, Integer> entry : droped.entrySet())
			sender.sendMessage(ChatColor.GRAY + entry.getKey().toString() + " found: " + entry.getValue());
		
		return true;
	}
}

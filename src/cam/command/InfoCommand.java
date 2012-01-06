package cam.command;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import cam.Likeaboss;
import cam.boss.BossManager;

public class InfoCommand extends CommandBase {

	public InfoCommand(Likeaboss plugin) {
		super(plugin);
	}

	public static boolean Process() {
		BossManager bossManager = plugin.getBossManager();
		
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Info");
		sender.sendMessage(ChatColor.GRAY + "Boss Killed: " + bossManager.getBossKilled());
		sender.sendMessage(ChatColor.GRAY + "Boss Count: " + bossManager.getBossCount());
			
		Map<Material, Integer> droped = bossManager.getDroped();
		for (Entry<Material, Integer> entry : droped.entrySet())
			sender.sendMessage(ChatColor.GRAY + entry.getKey().toString() + " found: " + entry.getValue());
		
		return true;
	}
}

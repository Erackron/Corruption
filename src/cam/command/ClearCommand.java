package cam.command;

import org.bukkit.ChatColor;

import cam.boss.BossManager;
import cam.drop.DropCalculator;

public abstract class ClearCommand extends CommandBase {

	public static void Process() {
		if (!CheckPermission("lab.clear", true))
			return;
			
		BossManager bossManager = plugin.getBossManager();
		DropCalculator dropCalculator = plugin.getDropCalculator();
		
		bossManager.Clear();
		dropCalculator.Clear();
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Cleared");
	}
}

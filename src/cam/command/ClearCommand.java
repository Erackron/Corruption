package cam.command;

import org.bukkit.ChatColor;

import cam.boss.BossManager;
import cam.boss.DropManager;

public abstract class ClearCommand extends CommandBase {

	public static boolean Process() {
		BossManager bossManager = plugin.getBossManager();
		DropManager dropManager = plugin.getDropManager();
		
		bossManager.Clear();
		dropManager.Clear();
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Cleared");
		
		return true;
	}
}

package cam.command;

import org.bukkit.ChatColor;

import cam.Likeaboss;
import cam.boss.BossManager;

public class ClearCommand extends CommandBase {

	public ClearCommand(Likeaboss plugin) {
		super(plugin);
	}

	public static boolean Process() {
		BossManager bossManager = plugin.getBossManager();
		
		bossManager.clear();
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Cleared");
		
		return true;
	}
}

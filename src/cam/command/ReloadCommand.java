package cam.command;

import org.bukkit.ChatColor;

import cam.boss.BossTaskManager;
import cam.player.LabPlayerTaskManager;

public abstract class ReloadCommand extends CommandBase {

	public static boolean Process() {		
		BossTaskManager bossTaskManager = plugin.getBossTaskManager();
		LabPlayerTaskManager labPlayerTaskManager = plugin.getLabPlayerTaskManager();
		
		bossTaskManager.Restart();
		labPlayerTaskManager.Restart();
		plugin.getLabConfig().LoadFiles();
		
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Reloaded");
		
		return true;
	}
}

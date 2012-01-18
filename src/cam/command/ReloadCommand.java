package cam.command;

import org.bukkit.ChatColor;

import cam.boss.BossTaskManager;
import cam.config.LabConfig;
import cam.player.LabPlayerTaskManager;

public abstract class ReloadCommand extends CommandBase {

	public static boolean Process() {
		LabConfig labConfig = new LabConfig(plugin);
		labConfig.LoadFiles();
		
		BossTaskManager bossTask = plugin.getBossTaskManager();
		bossTask.Restart();
		
		LabPlayerTaskManager labPlayerTaskManager = plugin.getLabPlayerTaskManager();
		labPlayerTaskManager.Restart();
		
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Reloaded");
		
		return true;
	}
}

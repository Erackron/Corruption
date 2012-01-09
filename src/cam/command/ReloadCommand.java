package cam.command;

import org.bukkit.ChatColor;

import cam.boss.BossTaskManager;
import cam.config.LabConfig;
import cam.player.LabPlayerTaskManager;

public abstract class ReloadCommand extends CommandBase {

	public static boolean Process() {
		LabConfig labConfig = plugin.getLabConfig();
		BossTaskManager bossTask = plugin.getBossTaskManager();
		LabPlayerTaskManager labPlayerTaskManager = plugin.getLabPlayerTaskManager();
		
		labConfig.LoadFile(plugin);
		bossTask.Restart();
		labPlayerTaskManager.Restart();
				
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Reloaded");
		
		return true;
	}
}

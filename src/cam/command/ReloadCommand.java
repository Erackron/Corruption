package cam.command;

import org.bukkit.ChatColor;

import cam.Likeaboss;
import cam.boss.BossTask;
import cam.config.LabConfig;

public class ReloadCommand extends CommandBase {

	public ReloadCommand(Likeaboss plugin) {
		super(plugin);
	}

	public static boolean Process() {
		BossTask bossTask = plugin.getBossTask();
		LabConfig labConfig = plugin.getLabConfig();
		
		bossTask.Stop();
		bossTask.Start(10, 10);
		labConfig.LoadFile(plugin);
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Reloaded");
		
		return true;
	}
}

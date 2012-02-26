package cam.command;

import org.bukkit.ChatColor;

import cam.task.TaskManager;

public abstract class ReloadCommand extends CommandBase {

	public static void Process() {
		if (!CheckPermission("lab.reload", true))
			return;
		
		TaskManager taskManager = plugin.getTaskManager();
		taskManager.Restart();
		
		plugin.getLabConfig().LoadFiles();
		
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Reloaded");
	}
}

package cam.command;

import org.bukkit.ChatColor;

import cam.Likeaboss;

public abstract class ReloadCommand extends BaseCommand {
	
	public static void Process() {
		if (!CheckPermission("lab.reload", true))
			return;
		
		Likeaboss.instance.getLabConfig().LoadFiles();
		Likeaboss.instance.getTaskManager().Restart();
		
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Reloaded");
	}
}

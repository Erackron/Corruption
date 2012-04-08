package cam.command;

import org.bukkit.ChatColor;

import cam.Likeaboss;

public abstract class ClearCommand extends BaseCommand {
	
	public static void Process() {
		if (!CheckPermission("lab.clear", true))
			return;
			
		Likeaboss.instance.getStats().Clear();
		
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Cleared");
	}
}

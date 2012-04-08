package cam.command;

import org.bukkit.ChatColor;

public abstract class HelpCommand extends BaseCommand {
	
	public static void Process() {
		if (!CheckPermission("lab.help", true))
			return;
		
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Commands list");
		sender.sendMessage("/lab clear: " + ChatColor.GRAY + "Clear informations given by /lab info.");
		sender.sendMessage("/lab ignore: " + ChatColor.GRAY + "Toggle ignore state, which allows to not be affected by bosses.");
		sender.sendMessage("/lab info: " + ChatColor.GRAY + "Display some global and non-lasting stats.");
		sender.sendMessage("/lab list: " + ChatColor.GRAY + "Display the location of active bosses.");
		sender.sendMessage("/lab reload: " + ChatColor.GRAY + "Reload configuration files.");
		sender.sendMessage("/lab spawn [type] <amount>: " + ChatColor.GRAY + "Spawn one or multiple bosses on the targeted block.");
		sender.sendMessage("/lab stats <player>: " + ChatColor.GRAY + "Display the leaderboard, or player stats.");
		sender.sendMessage("/lab viewer: " + ChatColor.GRAY + "Toggle viewer state, which allows to see boss healths.");
	}
}

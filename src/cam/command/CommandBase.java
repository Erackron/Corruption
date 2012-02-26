package cam.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cam.Likeaboss;

public abstract class CommandBase {
	
	protected static boolean processed = false;
	protected static Likeaboss plugin = null;
	protected static CommandSender sender = null;
	protected static String[] args = null;
	
	protected static boolean CheckPermission(String permission, boolean consoleUsage) {
		processed = true;
		
		if (!consoleUsage && !(sender instanceof Player)) {
			sender.sendMessage("[LAB] This command doesn't support console usage.");
			
			return false;
		}
		
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "You don't have the permission for this command. " +
							   ChatColor.GRAY + permission + ChatColor.WHITE + " is needed.");
			
			return false;
		}
		
		return true;
	}
}

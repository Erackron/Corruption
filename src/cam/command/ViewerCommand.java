package cam.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import cam.player.LabPlayer;
import cam.player.LabPlayerCommandStatus;
import cam.player.LabPlayerManager;

public abstract class ViewerCommand extends CommandBase {

	public static boolean Process() {
		LabPlayerManager labPlayerManager = plugin.getLabPlayerManager();
		LabPlayer labPlayer = labPlayerManager.getLabPlayer((Player) sender);
		
		if (labPlayer == null) {
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Oops, something went wrong.");
			sender.sendMessage("Please notify the plugin author.");
		}
		
		else {
			LabPlayerCommandStatus commandStatus = labPlayer.getCommandStatus();
			
			if (commandStatus.getViewer())
				commandStatus.setViewer(false);
			else
				commandStatus.setViewer(true);
			
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Viewer: " + ChatColor.GREEN + commandStatus.getViewer());
		}
		
		return true;
	}
}

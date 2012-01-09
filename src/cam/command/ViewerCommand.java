package cam.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import cam.player.LabPlayer;
import cam.player.LabPlayerManager;

public abstract class ViewerCommand extends CommandBase {

	public static boolean Process() {
		LabPlayerManager labPlayerManager = plugin.getLabPlayerManager();
		LabPlayer labPlayer = null;
		Player player = (Player) sender;
			
		if (labPlayerManager.IsLabPlayer(player)) {
			labPlayer = labPlayerManager.getLabPlayer(player);
				
			if (labPlayer.getViewer())
				labPlayer.setViewer(false);
			else
				labPlayer.setViewer(true);
			
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Viewer: " + ChatColor.GREEN + labPlayer.getViewer());
		}
			
		else {
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Oops, something went wrong.");
			sender.sendMessage("Please notify the plugin author.");
		}

		return true;
	}
}

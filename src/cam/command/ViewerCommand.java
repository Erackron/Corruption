package cam.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import cam.player.LabPlayer;
import cam.player.LabPlayerData;
import cam.player.LabPlayerManager;

public abstract class ViewerCommand extends CommandBase {

	public static void Process() {
		if (!CheckPermission("lab.viewer", false))
			return;
		
		LabPlayerManager labPlayerManager = plugin.getLabPlayerManager();
		LabPlayer labPlayer = labPlayerManager.getLabPlayer((Player) sender);
		
		if (labPlayer == null) {
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Oops, something went wrong.");
			sender.sendMessage("Please notify the plugin author.");
		}
		
		else {
			LabPlayerData labPlayerData = labPlayer.getLabPlayerData();
			boolean viewer = labPlayerData.getViewer();
			
			labPlayerData.setViewer(!viewer);
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Viewer: " + ChatColor.GREEN + !viewer);
		}
	}
}

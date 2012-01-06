package cam.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import cam.Likeaboss;
import cam.player.LabPlayer;
import cam.player.LabPlayerManager;

public class ViewerCommand extends CommandBase {

	public ViewerCommand(Likeaboss plugin) {
		super(plugin);
	}

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
		}
			
		else {
			labPlayer = new LabPlayer(player);
			labPlayer.setViewer(true);
			labPlayerManager.AddLabPlayer(labPlayer);
		}
			
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Viewer: " + ChatColor.GREEN + labPlayer.getViewer());

		return true;
	}
}

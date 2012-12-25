package cam.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import cam.Likeaboss;
import cam.config.GlobalConfig.CommandParam;
import cam.player.LabPlayer;
import cam.player.LabPlayerData;
import cam.player.LabPlayerManager;

public abstract class IgnoreCommand extends BaseCommand {
	public static void Process() {
		if (!CheckPermission("lab.ignore", false))
			return;
		
		LabPlayer labPlayer = LabPlayerManager.getLabPlayer((Player) sender);
		
		if (labPlayer == null) {
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Oops, something went wrong.");
			sender.sendMessage("Please notify the plugin author.");
			return;
		}
		
		int delay = CommandParam.IGNORE_DELAY.getValue();
		
		if (!sender.hasPermission("lab.ignore.immediate") && delay != 0) {
			int ignoreTaskId = labPlayer.getIgnoreTaskId();
			
			if (ignoreTaskId != 0) {
				Likeaboss.scheduler.cancelTask(ignoreTaskId);
				labPlayer.setIgnoreTaskId(0);
				sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Ignore: " + ChatColor.GRAY + "Canceled");
			}
			else {
				labPlayer.setIgnoreTaskId(Likeaboss.scheduler.scheduleSyncDelayedTask(Likeaboss.in, new IgnoreCommandTask(labPlayer), delay * 20));
				sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Ignore: " + ChatColor.GRAY + "Applied in " + ChatColor.GREEN + delay + ChatColor.GRAY + " second(s)");
			}
		}
		else
			Apply(labPlayer);
	}
	
	public static void Apply(LabPlayer labPlayer) {
		LabPlayerData labPlayerData = labPlayer.getLabPlayerData();
		boolean ignore = labPlayerData.getIgnore();
		
		labPlayerData.setIgnore(!ignore);
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Ignore: " + ChatColor.GREEN + !ignore);
	}
}

class IgnoreCommandTask implements Runnable {

	private LabPlayer labPlayer = null;
	
	public IgnoreCommandTask(LabPlayer labPlayer) {
		this.labPlayer = labPlayer;
	}
	
	@Override
	public void run() {
		IgnoreCommand.Apply(labPlayer);
		labPlayer.setIgnoreTaskId(0);
	}
}
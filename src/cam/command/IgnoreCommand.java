package cam.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import cam.config.GlobalConfig.CommandData;
import cam.player.LabPlayer;
import cam.player.LabPlayerData;
import cam.player.LabPlayerManager;

public abstract class IgnoreCommand extends CommandBase {

	public static void Process() {
		if (!CheckPermission("lab.ignore", false))
			return;
		
		LabPlayerManager labPlayerManager = plugin.getLabPlayerManager();
		LabPlayer labPlayer = labPlayerManager.getLabPlayer((Player) sender);
		BukkitScheduler bukkitScheduler = plugin.getServer().getScheduler();
		
		if (labPlayer == null) {
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Oops, something went wrong.");
			sender.sendMessage("Please notify the plugin author.");
			return;
		}
		
		int delay = CommandData.IGNORE_DELAY.getValue();
		
		if (!sender.hasPermission("lab.ignore.immediate") && delay != 0) {
			int ignoreTaskId = labPlayer.getIgnoreTaskId();
			
			if (ignoreTaskId != 0) {
				bukkitScheduler.cancelTask(ignoreTaskId);
				labPlayer.setIgnoreTaskId(0);
				sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Ignore: " + ChatColor.GRAY + "Canceled");
			}
			else {
				labPlayer.setIgnoreTaskId(bukkitScheduler.scheduleAsyncDelayedTask(plugin, new IgnoreCommandTask(labPlayer), delay * 20));
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
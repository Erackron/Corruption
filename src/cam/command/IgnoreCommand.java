package cam.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import cam.player.LabPlayer;
import cam.player.LabPlayerCommandStatus;
import cam.player.LabPlayerManager;

public abstract class IgnoreCommand extends CommandBase {

	private static int delay = 0;
	
	public static boolean Process(boolean immediate) {
		LabPlayerManager labPlayerManager = plugin.getLabPlayerManager();
		LabPlayer labPlayer = labPlayerManager.getLabPlayer((Player) sender);
		BukkitScheduler bukkitScheduler = plugin.getServer().getScheduler();
		
		if (labPlayer == null) {
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Oops, something went wrong.");
			sender.sendMessage("Please notify the plugin author.");
			return true;
		}
		
		if (!immediate && delay != 0) {
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
		
		return true;
	}
	
	public static void Apply(LabPlayer labPlayer) {
		LabPlayerCommandStatus commandStatus = labPlayer.getCommandStatus();
		
		if (commandStatus.getIgnore())
			commandStatus.setIgnore(false);
		else
			commandStatus.setIgnore(true);
			
		sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Ignore: " + ChatColor.GREEN + commandStatus.getIgnore());
	}
	
	public static void setDelay(int delay) {
		IgnoreCommand.delay = delay;
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
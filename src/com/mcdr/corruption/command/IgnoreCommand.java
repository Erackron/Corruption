package com.mcdr.corruption.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.GlobalConfig.CommandParam;
import com.mcdr.corruption.player.CorPlayer;
import com.mcdr.corruption.player.CorPlayerData;
import com.mcdr.corruption.player.CorPlayerManager;


public abstract class IgnoreCommand extends BaseCommand {
	public static void Process() {
		if (!checkPermission("cor.ignore", false))
			return;
		
		CorPlayer corPlayer = CorPlayerManager.getCorPlayer((Player) sender);
		
		if (corPlayer == null) {
			sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + "Oops, something went wrong.");
			sender.sendMessage("Please notify the plugin author.");
			return;
		}
		
		int delay = CommandParam.IGNORE_DELAY.getValue();
		
		if (!sender.hasPermission("cor.ignore.immediate") && delay != 0) {
			int ignoreTaskId = corPlayer.getIgnoreTaskId();
			
			if (ignoreTaskId != 0) {
				Corruption.scheduler.cancelTask(ignoreTaskId);
				corPlayer.setIgnoreTaskId(0);
				sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + "Ignore: " + ChatColor.GRAY + "Canceled");
			}
			else {
				corPlayer.setIgnoreTaskId(Corruption.scheduler.scheduleSyncDelayedTask(Corruption.in, new IgnoreCommandTask(corPlayer), delay * 20));
				sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + "Ignore: " + ChatColor.GRAY + "Applied in " + ChatColor.GREEN + delay + ChatColor.GRAY + " second(s)");
			}
		}
		else
			Apply(corPlayer);
	}
	
	public static void Apply(CorPlayer labPlayer) {
		CorPlayerData labPlayerData = labPlayer.getCorPlayerData();
		boolean ignore = labPlayerData.getIgnore();
		if(!ignore)
			for(Entity e :labPlayer.getPlayer().getNearbyEntities(32, 32, 32))
				if(e instanceof Creature)
					((Creature) e).setTarget(null);
					
		labPlayerData.setIgnore(!ignore);
		sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + "Ignore: " + ChatColor.GREEN + !ignore);
	}
}

class IgnoreCommandTask implements Runnable {

	private CorPlayer labPlayer = null;
	
	public IgnoreCommandTask(CorPlayer labPlayer) {
		this.labPlayer = labPlayer;
	}
	
	@Override
	public void run() {
		IgnoreCommand.Apply(labPlayer);
		labPlayer.setIgnoreTaskId(0);
	}
}
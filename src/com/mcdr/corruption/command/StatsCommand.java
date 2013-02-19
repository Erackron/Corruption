package com.mcdr.corruption.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.player.CorPlayer;
import com.mcdr.corruption.task.TaskManager;
import com.mcdr.corruption.util.Utility;



public abstract class StatsCommand extends BaseCommand {
	public static void process() {
		if (!checkPermission("cor.stats", true))
			return;
		
		if (args.length < 2) {
			Corruption.scheduler.runTask(Corruption.in, new GlobalStatsDisplayer(sender));
		}
		else {
			CorPlayer corPlayer = new CorPlayer(Corruption.in.getServer().getOfflinePlayer(args[1]));

			Corruption.scheduler.runTask(Corruption.in, new IndividualStatsDisplayer(sender, corPlayer));
		}
	}
	
	private static class GlobalStatsDisplayer extends StatsCommand.StatsDisplayer {
		public GlobalStatsDisplayer(CommandSender sender) {
			super(sender);
		}

		public void run()
		{
			if (!RetrievePlayerData()) {
				Corruption.scheduler.runTaskLater(Corruption.in, this, 5L);
				return;
			}

			for (CorPlayer CorPlayer : this.CorPlayerList) {
				int amount = CorPlayer.getTotalBossesKilled();

				if (amount > 0) {
					this.unsortedMap.put(CorPlayer.getName(), Integer.valueOf(amount));
				}
			}

			this.sender.sendMessage(ChatColor.GOLD + "[" + Corruption.in.getName() + "] " + ChatColor.WHITE + "Leaderboard (" + ChatColor.GREEN + "Bosses Killed" + ChatColor.WHITE + ")");
			DisplayStats();
			this.CorPlayerList.clear();
		}
	}

	private static class IndividualStatsDisplayer extends StatsCommand.StatsDisplayer {
		private String playerName;

		public IndividualStatsDisplayer(CommandSender sender, CorPlayer CorPlayer) {
			super(sender);
			this.playerName = CorPlayer.getName();
			this.CorPlayerList = Arrays.asList(new CorPlayer[] { CorPlayer });
		}

	@SuppressWarnings("rawtypes")
	public void run()
		{
			if (!RetrievePlayerData()) {
				Corruption.scheduler.runTaskLater(Corruption.in, this, 5L);
				return;
			}

			if (this.CorPlayerList.isEmpty()) {
				this.sender.sendMessage(ChatColor.GOLD + "[" + Corruption.in.getName() + "] " + ChatColor.GRAY + this.playerName + ChatColor.WHITE + " isn't a valid player or doesn't have any stats yet.");
				return;
			}

			CorPlayer CorPlayer = (CorPlayer)this.CorPlayerList.get(0);

			for (Entry entry : CorPlayer.getBossesKilled().entrySet()) {
				this.unsortedMap.put((String)entry.getKey(), (Integer)entry.getValue());
			}

			this.sender.sendMessage(ChatColor.GOLD + "[" + Corruption.in.getName() + "] " + ChatColor.WHITE + this.playerName + " (" + ChatColor.GREEN + "Bosses Killed" + ChatColor.WHITE + ")");
			DisplayStats();
			this.CorPlayerList.clear();
		}
	}

	private static abstract class StatsDisplayer
		implements Runnable
	{
		protected CommandSender sender;
		protected List<CorPlayer> CorPlayerList;
		protected short requestId;
		Map<String, Integer> unsortedMap = new HashMap<String, Integer>();

		public StatsDisplayer(CommandSender sender) {
			this.sender = sender;
		}

		protected boolean RetrievePlayerData() {
			if (this.requestId == 0) {
				this.requestId = TaskManager.getCorPlayerFileAccessor().initiatePlayerDataLoading(this.CorPlayerList);

				return false;
			}

			this.CorPlayerList = TaskManager.getCorPlayerFileAccessor().getResult(this.requestId);

			if (this.CorPlayerList == null) {
				return false;
			}

			return true;
		}

	@SuppressWarnings("rawtypes")
	protected void DisplayStats() {
			Iterator it = Utility.sortEntriesByValues(this.unsortedMap, false).iterator();

			for (int i = 1; (i <= 10) && (it.hasNext()); i++) {
				Entry entry = (Entry)it.next();
				String message = ChatColor.GRAY + String.valueOf(i) + ". " + ChatColor.WHITE + (String)entry.getKey() + " (" + ChatColor.GREEN + entry.getValue() + ChatColor.WHITE + ")";

				this.sender.sendMessage(message);
			}
		}
	}
}

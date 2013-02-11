package com.mcdr.corruption.command;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.player.CorPlayer;
import com.mcdr.corruption.player.CorPlayerManager;
import com.mcdr.corruption.util.Utility;



public abstract class StatsCommand extends BaseCommand {
	public static void Process() {
		if (!checkPermission("cor.stats", true))
			return;
		
		Map<String, Integer> unsortedMap = new HashMap<String, Integer>();
		List<CorPlayer> tempcorPlayers = new ArrayList<CorPlayer>();
		
		tempcorPlayers.addAll(CorPlayerManager.getCorPlayers());
		tempcorPlayers.addAll(CorPlayerManager.getSeenCorPlayers());
		
		if (args.length < 2) {
			sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + "Leaderboard (" + ChatColor.GREEN + "Bosses Killed" + ChatColor.WHITE + ")");
			
			for (CorPlayer corPlayer : tempcorPlayers) {
				int amount = corPlayer.getTotalBossesKilled();
				
				if (amount > 0)
					unsortedMap.put(corPlayer.getName(), amount);
			}
			
			File file = CorPlayerManager.getFile();
			Scanner scanner = null;
			
			try {
				scanner = new Scanner(new FileInputStream(file));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] data = CorPlayerManager.getPattern().split(line);
				
				if (data.length >= 0 && !unsortedMap.containsKey(data[0])) {
					CorPlayer labPlayer = new CorPlayer(Bukkit.getOfflinePlayer(data[0]));
					
					try {
						CorPlayerManager.LoadPlayerData(labPlayer);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					int amount = labPlayer.getTotalBossesKilled();
					
					if (amount > 0)
						unsortedMap.put(labPlayer.getName(), amount);
				}
			}
			
			scanner.close();
		} else {
			Map<String, Integer> playerStats = null;
			
			for (CorPlayer labPlayer : tempcorPlayers) {
				if (labPlayer.getName().equalsIgnoreCase(args[1])) {
					playerStats = labPlayer.getBossesKilled();
					break;
				}
			}
			
			if (playerStats == null) {
				File file = CorPlayerManager.getFile();
				Scanner scanner = null;
				
				try {
					scanner = new Scanner(new FileInputStream(file));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					String[] data = CorPlayerManager.getPattern().split(line);
					
					if (data.length >= 0 && data[0].equalsIgnoreCase(args[1])) {
						CorPlayer labPlayer = new CorPlayer(Bukkit.getOfflinePlayer(args[1]));
						
						try {
							CorPlayerManager.LoadPlayerData(labPlayer);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						playerStats = labPlayer.getBossesKilled();
						break;
					}
				}
				
				scanner.close();
			}
			
			if (playerStats == null) {
				sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.GRAY + args[1] + ChatColor.WHITE + " isn't a valid player or doesn't have any stats yet.");
				return;
			}
			
			sender.sendMessage(ChatColor.GOLD + "["+Corruption.in.getName()+"] " + ChatColor.WHITE + args[1] + " (" + ChatColor.GREEN + "Bosses Killed" + ChatColor.WHITE + ")");
			
			for (Entry<String, Integer> entry : playerStats.entrySet())
				unsortedMap.put(entry.getKey(), entry.getValue());
		}
		
		Set<Entry<String, Integer>> sortedEntries = Utility.SortEntriesByValues(unsortedMap, false);
		Iterator<Entry<String, Integer>> it = sortedEntries.iterator();
		
		for (int i = 1 ; i <= 10 ; i++) {
			if (!it.hasNext())
				break;
			
			Entry<String, Integer> entry = it.next();
			
			sender.sendMessage(ChatColor.GRAY + "" + i + ". " + ChatColor.WHITE + Utility.parseMessage("{BOSSNAME}", entry.getKey()) + " (" + ChatColor.GREEN + entry.getValue() + ChatColor.WHITE + ")");
		}
	}
}

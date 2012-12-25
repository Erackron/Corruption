package cam.command;

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

import cam.Utility;

import cam.player.LabPlayer;
import cam.player.LabPlayerManager;

public abstract class StatsCommand extends BaseCommand {
	public static void Process() {
		if (!CheckPermission("lab.stats", true))
			return;
		
		Map<String, Integer> unsortedMap = new HashMap<String, Integer>();
		List<LabPlayer> tempLabPlayers = new ArrayList<LabPlayer>();
		
		tempLabPlayers.addAll(LabPlayerManager.getLabPlayers());
		tempLabPlayers.addAll(LabPlayerManager.getSeenLabPlayers());
		
		if (args.length < 2) {
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Leaderboard (" + ChatColor.GREEN + "Bosses Killed" + ChatColor.WHITE + ")");
			
			for (LabPlayer labPlayer : tempLabPlayers) {
				int amount = labPlayer.getTotalBossesKilled();
				
				if (amount > 0)
					unsortedMap.put(labPlayer.getName(), amount);
			}
			
			File file = LabPlayerManager.getFile();
			Scanner scanner = null;
			
			try {
				scanner = new Scanner(new FileInputStream(file));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] data = LabPlayerManager.getPattern().split(line);
				
				if (data.length >= 0 && !unsortedMap.containsKey(data[0])) {
					LabPlayer labPlayer = new LabPlayer(Bukkit.getOfflinePlayer(data[0]));
					
					try {
						LabPlayerManager.LoadPlayerData(labPlayer);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					int amount = labPlayer.getTotalBossesKilled();
					
					if (amount > 0)
						unsortedMap.put(labPlayer.getName(), amount);
				}
			}
			
			scanner.close();
		}
		
		else {
			Map<String, Integer> playerStats = null;
			
			for (LabPlayer labPlayer : tempLabPlayers) {
				if (labPlayer.getName().equalsIgnoreCase(args[1])) {
					playerStats = labPlayer.getBossesKilled();
					break;
				}
			}
			
			if (playerStats == null) {
				File file = LabPlayerManager.getFile();
				Scanner scanner = null;
				
				try {
					scanner = new Scanner(new FileInputStream(file));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					String[] data = LabPlayerManager.getPattern().split(line);
					
					if (data.length >= 0 && data[0].equalsIgnoreCase(args[1])) {
						LabPlayer labPlayer = new LabPlayer(Bukkit.getOfflinePlayer(args[1]));
						
						try {
							LabPlayerManager.LoadPlayerData(labPlayer);
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
				sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.GRAY + args[1] + ChatColor.WHITE + " isn't a valid player or doesn't have any stats yet.");
				return;
			}
			
			sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + args[1] + " (" + ChatColor.GREEN + "Bosses Killed" + ChatColor.WHITE + ")");
			
			for (Entry<String, Integer> entry : playerStats.entrySet())
				unsortedMap.put(entry.getKey(), entry.getValue());
		}
		
		Set<Entry<String, Integer>> sortedEntries = Utility.SortEntriesByValues(unsortedMap, false);
		Iterator<Entry<String, Integer>> it = sortedEntries.iterator();
		
		for (int i = 1 ; i <= 10 ; i++) {
			if (!it.hasNext())
				break;
			
			Entry<String, Integer> entry = it.next();
			
			sender.sendMessage(ChatColor.GRAY + "" + i + ". " + ChatColor.WHITE + entry.getKey() + " (" + ChatColor.GREEN + entry.getValue() + ChatColor.WHITE + ")");
		}
	}
}

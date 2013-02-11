package com.mcdr.corruption.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.GlobalConfig.MessageParam;
import com.mcdr.corruption.util.Utility;


public abstract class CorPlayerManager {
	private static List<CorPlayer> corPlayers = new ArrayList<CorPlayer>();
	private static List<CorPlayer> seenCorPlayers = new ArrayList<CorPlayer>();
	private static Pattern pattern = Pattern.compile(StringConst.SEPARATOR.getString());
	
	private enum StringConst {
		FILEPATH {@Override public String getString() {return Corruption.in.getDataFolder().getPath() + File.separator + "players.dat";}},
		SEPARATOR {@Override public String getString() {return ":";}},
		ENDLINE {@Override public String getString() {return System.getProperty("line.separator");}};
		
		public abstract String getString();
	}
	
	public static void AddOnlinePlayers() {
		Player[] players = Corruption.in.getServer().getOnlinePlayers();
		
		for (Player player : players)
			addCorPlayer(player);
	}
	
	public static CorPlayer addCorPlayer(Player player) {
		for (CorPlayer corPlayer : seenCorPlayers) {
			if (corPlayer.getName().equals(player.getName())) {
				corPlayer.setPlayer(player);
				corPlayers.add(corPlayer);
				seenCorPlayers.remove(corPlayer);
				return corPlayer;
			}
		}
		
		CorPlayer corPlayer = new CorPlayer(player);
		corPlayers.add(corPlayer);
		try {
			LoadPlayerData(corPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return corPlayer;
	}
	
	public static void removeCorPlayer(Player player) {
		for (CorPlayer corPlayer : corPlayers) {
			if (corPlayer.getName().equals(player.getName())) {
				corPlayers.remove(corPlayer);
				seenCorPlayers.add(corPlayer);
				return;
			}
		}
	}
	
	public static void LoadPlayerData(CorPlayer corPlayer) {
		File file = getFile();
		Scanner scanner = null;
		
		try {
			scanner = new Scanner(new FileInputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			
			if (line == StringConst.ENDLINE.getString())
				continue;
			
			String name = line.substring(0, line.indexOf(StringConst.SEPARATOR.getString()));
			
			if (!name.equalsIgnoreCase(corPlayer.getName()))
				continue;
			
			String[] data = pattern.split(line);
			
			if (data.length >= 3) {
				CorPlayerData corPlayerData = new CorPlayerData();
				corPlayerData.setViewer(Boolean.valueOf(data[1]));
				corPlayerData.setIgnore(Boolean.valueOf(data[2]));
				corPlayer.setCorPlayerData(corPlayerData);
			}
			
			for (int i = 3 ; i < data.length ; i += 2)
				corPlayer.AddBossKilled(data[i], Integer.valueOf(data[i+1]));
			
			break;
		}
		
		scanner.close();
	}
	
	public static void SavePlayerData() throws Exception {
		String separator = StringConst.SEPARATOR.getString();
		String endLine = StringConst.ENDLINE.getString();
		File file = getFile();
		File tempFile = new File(file.getPath() + ".temp");
		
		Utility.fileToFile(file, tempFile);
		
		//Copy file A to B and overwrite A.
		Scanner scanner = new Scanner(new FileInputStream(tempFile));
		Writer writer = new OutputStreamWriter(new FileOutputStream(file, false));
		
		seenCorPlayers.addAll(corPlayers);
		
		Outer:
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			
			if (line == endLine)
				continue;
			
			String nameInFile = line.substring(0, line.indexOf(separator));
			
			for (CorPlayer corPlayer : seenCorPlayers) {
				String playerName = corPlayer.getName();
				
				//If the player is present in the temp file and was seen, update its data
				if (nameInFile.equals(playerName)) {
					CorPlayerData commandStatus = corPlayer.getCorPlayerData();
					
					writer.write(playerName + separator + commandStatus.getViewer() + separator + commandStatus.getIgnore() + separator);
					
					Map<String, Integer> bossesKilled = corPlayer.getBossesKilled();
					
					for (Entry<String, Integer> bossKilled : bossesKilled.entrySet())
						writer.write(bossKilled.getKey() + separator + bossKilled.getValue() + separator);
					
					writer.write(endLine);
					
					seenCorPlayers.remove(corPlayer);
					continue Outer;
				}
			}
			
			//If not seen, just copy the line
			writer.write(line + endLine);
		}
		
		scanner.close();
		
		//New players
		for (CorPlayer corPlayer : seenCorPlayers) {
			CorPlayerData commandStatus = corPlayer.getCorPlayerData();
			
			writer.write(corPlayer.getName() + separator + commandStatus.getViewer() + separator + commandStatus.getIgnore() + separator);
				
			Map<String, Integer> bossesKilled = corPlayer.getBossesKilled();
			
			for (Entry<String, Integer> bossKilled : bossesKilled.entrySet())
				writer.write(bossKilled.getKey() + separator + bossKilled.getValue() + separator);
			
			writer.write(endLine);
		}
		
		seenCorPlayers.clear();
		
		writer.close();
	}
	
	public static void SendFoundMessage(CorPlayer corPlayer, boolean isFinder, Location location, String bossName) {
		String toPlayer = null;
		String toOthers = null;
		
		bossName = (bossName.contains("#"))?bossName.split("#")[0]:bossName;
		String[] bNameS = bossName.split("(?=\\p{Upper})");
		if (bNameS.length>1){
			bossName = bNameS[1];
			for (int i = 2 ; i < bNameS.length ; i++) 
				bossName += " "+bNameS[i];
		}

		if (isFinder) {
			toPlayer = MessageParam.PLAYER_FOUND_BOSS_1.getMessage();
			toOthers = MessageParam.PLAYER_FOUND_BOSS_2.getMessage();
		}
		else {
			toPlayer = MessageParam.BOSS_FOUND_PLAYER_1.getMessage();
			toOthers = MessageParam.BOSS_FOUND_PLAYER_2.getMessage();
		}
		
		if (toPlayer.length() > 0) {
			toPlayer = toPlayer.replace('&', ChatColor.COLOR_CHAR).replace("{BOSSNAME}", bossName);
			
			
			corPlayer.getPlayer().sendMessage(toPlayer);
		}
		
		if (toOthers.length() > 0) {
			corPlayers.remove(corPlayer);
			
			toOthers = toOthers.replace('&', ChatColor.COLOR_CHAR).replace("{PLAYER}", corPlayer.getPlayer().getDisplayName()).replace("{BOSSNAME}", bossName);
			
			for (CorPlayer otherCorPlayer : corPlayers) {
				Player otherPlayer = otherCorPlayer.getPlayer();
				
				if (Utility.isNear(otherPlayer.getLocation(), location, 0, 35))
					otherPlayer.sendMessage(toOthers);
			}
			
			corPlayers.add(corPlayer);
		}
	}
	
	public static CorPlayer getCorPlayer(Entity entity) {
		for (CorPlayer corPlayer : corPlayers) {
			if (corPlayer.getPlayer() == entity)
				return corPlayer;
		}
		
		return null;
	}
	
	public static List<CorPlayer> getCorPlayers() {
		return corPlayers;
	}
	
	public static List<CorPlayer> getSeenCorPlayers() {
		return seenCorPlayers;
	}
	
	public static File getFile() {
		File file = new File(StringConst.FILEPATH.getString());
		
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (!parentFile.exists())
				parentFile.mkdirs();
			
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return file;
	}
	
	public static Pattern getPattern() {
		return pattern;
	}
}

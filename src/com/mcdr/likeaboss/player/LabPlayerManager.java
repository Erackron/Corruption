package com.mcdr.likeaboss.player;

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

import com.mcdr.likeaboss.Likeaboss;
import com.mcdr.likeaboss.config.GlobalConfig.MessageParam;
import com.mcdr.likeaboss.util.Utility;


public abstract class LabPlayerManager {
	private static List<LabPlayer> labPlayers = new ArrayList<LabPlayer>();
	private static List<LabPlayer> seenLabPlayers = new ArrayList<LabPlayer>();
	private static Pattern pattern = Pattern.compile(StringConst.SEPARATOR.getString());
	
	private enum StringConst {
		FILEPATH {@Override public String getString() {return Likeaboss.in.getDataFolder().getPath() + File.separator + "players.dat";}},
		SEPARATOR {@Override public String getString() {return ":";}},
		ENDLINE {@Override public String getString() {return System.getProperty("line.separator");}};
		
		public abstract String getString();
	}
	
	public static void AddOnlinePlayers() {
		Player[] players = Likeaboss.in.getServer().getOnlinePlayers();
		
		for (Player player : players)
			AddLabPlayer(player);
	}
	
	public static LabPlayer AddLabPlayer(Player player) {
		for (LabPlayer labPlayer : seenLabPlayers) {
			if (labPlayer.getName().equals(player.getName())) {
				labPlayer.setPlayer(player);
				labPlayers.add(labPlayer);
				seenLabPlayers.remove(labPlayer);
				return labPlayer;
			}
		}
		
		LabPlayer labPlayer = new LabPlayer(player);
		labPlayers.add(labPlayer);
		try {
			LoadPlayerData(labPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return labPlayer;
	}
	
	public static void RemoveLabPlayer(Player player) {
		for (LabPlayer labPlayer : labPlayers) {
			if (labPlayer.getName().equals(player.getName())) {
				labPlayers.remove(labPlayer);
				seenLabPlayers.add(labPlayer);
				return;
			}
		}
	}
	
	public static void LoadPlayerData(LabPlayer labPlayer) {
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
			
			if (!name.equalsIgnoreCase(labPlayer.getName()))
				continue;
			
			String[] data = pattern.split(line);
			
			if (data.length >= 3) {
				LabPlayerData labPlayerData = new LabPlayerData();
				labPlayerData.setViewer(Boolean.valueOf(data[1]));
				labPlayerData.setIgnore(Boolean.valueOf(data[2]));
				labPlayer.setLabPlayerData(labPlayerData);
			}
			
			for (int i = 3 ; i < data.length ; i += 2)
				labPlayer.AddBossKilled(data[i], Integer.valueOf(data[i+1]));
			
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
		
		seenLabPlayers.addAll(labPlayers);
		
		Outer:
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			
			if (line == endLine)
				continue;
			
			String nameInFile = line.substring(0, line.indexOf(separator));
			
			for (LabPlayer labPlayer : seenLabPlayers) {
				String playerName = labPlayer.getName();
				
				//If the player is present in the temp file and was seen, update its data
				if (nameInFile.equals(playerName)) {
					LabPlayerData commandStatus = labPlayer.getLabPlayerData();
					
					writer.write(playerName + separator + commandStatus.getViewer() + separator + commandStatus.getIgnore() + separator);
					
					Map<String, Integer> bossesKilled = labPlayer.getBossesKilled();
					
					for (Entry<String, Integer> bossKilled : bossesKilled.entrySet())
						writer.write(bossKilled.getKey() + separator + bossKilled.getValue() + separator);
					
					writer.write(endLine);
					
					seenLabPlayers.remove(labPlayer);
					continue Outer;
				}
			}
			
			//If not seen, just copy the line
			writer.write(line + endLine);
		}
		
		scanner.close();
		
		//New players
		for (LabPlayer labPlayer : seenLabPlayers) {
			LabPlayerData commandStatus = labPlayer.getLabPlayerData();
			
			writer.write(labPlayer.getName() + separator + commandStatus.getViewer() + separator + commandStatus.getIgnore() + separator);
				
			Map<String, Integer> bossesKilled = labPlayer.getBossesKilled();
			
			for (Entry<String, Integer> bossKilled : bossesKilled.entrySet())
				writer.write(bossKilled.getKey() + separator + bossKilled.getValue() + separator);
			
			writer.write(endLine);
		}
		
		seenLabPlayers.clear();
		
		writer.close();
	}
	
	public static void SendFoundMessage(LabPlayer labPlayer, boolean isFinder, Location location, String bossName) {
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
			
			
			labPlayer.getPlayer().sendMessage(toPlayer);
		}
		
		if (toOthers.length() > 0) {
			labPlayers.remove(labPlayer);
			
			toOthers = toOthers.replace('&', ChatColor.COLOR_CHAR).replace("{PLAYER}", labPlayer.getPlayer().getDisplayName()).replace("{BOSSNAME}", bossName);
			
			for (LabPlayer otherLabPlayer : labPlayers) {
				Player otherPlayer = otherLabPlayer.getPlayer();
				
				if (Utility.isNear(otherPlayer.getLocation(), location, 0, 35))
					otherPlayer.sendMessage(toOthers);
			}
			
			labPlayers.add(labPlayer);
		}
	}
	
	public static LabPlayer getLabPlayer(Entity entity) {
		for (LabPlayer labPlayer : labPlayers) {
			if (labPlayer.getPlayer() == entity)
				return labPlayer;
		}
		
		return null;
	}
	
	public static List<LabPlayer> getLabPlayers() {
		return labPlayers;
	}
	
	public static List<LabPlayer> getSeenLabPlayers() {
		return seenLabPlayers;
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

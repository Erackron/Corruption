package cam.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import cam.Likeaboss;
import cam.Utility;
import cam.config.GlobalConfig.MessageData;

public class LabPlayerManager {

	private static List<LabPlayer> labPlayers = new ArrayList<LabPlayer>();
	private static List<LabPlayer> seenLabPlayers = new ArrayList<LabPlayer>();
	private static Map<String, LabPlayerData> labPlayerData = new HashMap<String, LabPlayerData>();
	private static String filePath = "plugins/Likeaboss/players.dat";
	private static String separator = ":";
	private static String endLine = System.getProperty("line.separator");
	
	public LabPlayerManager() {
	}
	
	public void AddOnlinePlayers(Likeaboss plugin) {
		Player[] players = plugin.getServer().getOnlinePlayers();
		
		for (Player player : players)
			AddLabPlayer(player);
	}
	
	public void AddLabPlayer(Player player) {
		//for (LabPlayer labPlayer : seenLabPlayers) {
		//	if (labPlayer.getPlayer() == player) {
		//		labPlayers.add(labPlayer);
		//		return;
		//	}
		//}
		for (LabPlayer labPlayer : seenLabPlayers) {
			if (labPlayer.getPlayer().getName().equals(player.getName())) {
				labPlayer.setPlayer(player);
				labPlayers.add(labPlayer);
				return;
			}
		}
		
		LabPlayer labPlayer = new LabPlayer(player);
		labPlayers.add(labPlayer);
		seenLabPlayers.add(labPlayer);
		
		for (Entry<String, LabPlayerData> entry : labPlayerData.entrySet()) {
			if (entry.getKey().equals(player.getName())) {
				labPlayer.setLabPlayerData(entry.getValue());
				return;
			}
		}
	}
	
	public void RemoveLabPlayer(Player player) {
		for (LabPlayer labPlayer : labPlayers) {
			if (labPlayer.getPlayer() == player) {
				labPlayers.remove(labPlayer);
				return;
			}
		}
	}
	
	public void LoadFile() throws Exception {
		File file = new File(filePath);
		
		if (!file.exists())
			return;
		
		Scanner scanner = new Scanner(new FileInputStream(file));
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] data = line.split(separator);
			
			LabPlayerData commandStatus = new LabPlayerData();
			commandStatus.setViewer(Boolean.valueOf(data[1]));
			commandStatus.setIgnore(Boolean.valueOf(data[2]));
			
			labPlayerData.put(data[0], commandStatus);
		}
		
		scanner.close();
	}
	
	public void SaveFile() throws Exception {
		File file = new File(filePath);
		
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (!parentFile.exists())
				parentFile.mkdirs();
			
			file.createNewFile();
		}
		
		File tempFile = new File(filePath + ".old");
		Utility.FileToFile(file, tempFile);
		
		Scanner scanner = new Scanner(new FileInputStream(tempFile));
		Writer writer = new OutputStreamWriter(new FileOutputStream(file, false));
		
		Outer:
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			
			if (line == endLine)
				continue;
			
			String nameInFile = line.substring(0, line.indexOf(separator));
			
			for (LabPlayer labPlayer : seenLabPlayers) {
				String playerName = labPlayer.getPlayer().getName();
				
				//If the player is present in the old file and was seen, update its data
				if (nameInFile.equals(playerName)) {
					LabPlayerData commandStatus = labPlayer.getLabPlayerData();
					String viewer = String.valueOf(commandStatus.getViewer());
					String ignore = String.valueOf(commandStatus.getIgnore());
					
					writer.write(playerName + separator + viewer + separator + ignore + endLine);
					seenLabPlayers.remove(labPlayer);
					continue Outer;
				}
			}
			
			//If not seen, just copy the line
			writer.write(line + endLine);
		}
		
		//New players
		for (LabPlayer labPlayer : seenLabPlayers) {
			LabPlayerData commandStatus = labPlayer.getLabPlayerData();
			
			String playerName = labPlayer.getPlayer().getName();
			String viewer = String.valueOf(commandStatus.getViewer());
			String ignore = String.valueOf(commandStatus.getIgnore());
			
			writer.write(playerName + separator + viewer + separator + ignore + endLine);
		}
		
		scanner.close();
		writer.close();
	}
	
	public void SendFoundMessage(Player player, boolean isFinder, Entity entity) {
		String toPlayer = null;
		String toOthers = null;
		
		if (isFinder) {
			toPlayer = MessageData.PLAYER_FOUND_BOSS_1.getMessage();
			toOthers = MessageData.PLAYER_FOUND_BOSS_2.getMessage();
		}
		else {
			toPlayer = MessageData.BOSS_FOUND_PLAYER_1.getMessage();
			toOthers = MessageData.BOSS_FOUND_PLAYER_2.getMessage();
		}
		
		toPlayer = toPlayer.replace('&', ChatColor.COLOR_CHAR);
		toOthers = toOthers.replace('&', ChatColor.COLOR_CHAR).replace("{PLAYER}", player.getDisplayName());
		
		for (LabPlayer otherLabPlayer : labPlayers) {
			Player otherPlayer = otherLabPlayer.getPlayer();
			
			if (otherPlayer == player)
				player.sendMessage(toPlayer);
			
			else if (Utility.IsNear(otherPlayer.getLocation(), entity.getLocation(), 0, 35))
				otherPlayer.sendMessage(toOthers);
		}
	}

	public LabPlayer getLabPlayer(Entity entity) {
		for (LabPlayer labPlayer : labPlayers) {
			if (labPlayer.getPlayer() == entity)
				return labPlayer;
		}
		
		return null;
	}
	
	public List<LabPlayer> getLabPlayers() {
		return labPlayers;
	}
}

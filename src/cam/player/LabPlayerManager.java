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
import cam.config.MessageData;

public class LabPlayerManager {

	private static List<LabPlayer> labPlayers = new ArrayList<LabPlayer>();
	private static List<LabPlayer> seenLabPlayers = new ArrayList<LabPlayer>();
	private static Map<String, LabPlayerCommandStatus> labPlayerCommandStatus = new HashMap<String, LabPlayerCommandStatus>();
	private static String filePath = "plugins/Likeaboss/players.dat";
	private static String separator = ":";
	private static String endLine = System.getProperty("line.separator");
	
	public LabPlayerManager() {
	}
	
	public void AddOnlinePlayers(Likeaboss plugin) {
		Object[] players = plugin.getServer().getOnlinePlayers();
		
		for (Object player : players)
			AddLabPlayer((Player) player);
	}
	
	public void AddLabPlayer(Player player) {
		for (LabPlayer labPlayer : seenLabPlayers) {
			if (labPlayer.getPlayer() == player) {
				labPlayers.add(labPlayer);
				return;
			}
		}
		
		LabPlayer labPlayer = new LabPlayer(player);
		labPlayers.add(labPlayer);
		seenLabPlayers.add(labPlayer);
		
		for (Entry<String, LabPlayerCommandStatus> entry : labPlayerCommandStatus.entrySet()) {
			if (entry.getKey().equals(player.getName())) {
				labPlayer.setCommandsStatus(entry.getValue());
				break;
			}
		}
	}
	
	public void RemoveLabPlayer(Player player) {
		for (LabPlayer labPlayer : labPlayers) {
			if (labPlayer.getPlayer() == player) {
				labPlayers.remove(labPlayer);
				break;
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
			
			LabPlayerCommandStatus commandStatus = new LabPlayerCommandStatus();
			commandStatus.setViewer(Boolean.valueOf(data[1]));
			commandStatus.setIgnore(Boolean.valueOf(data[2]));
			
			labPlayerCommandStatus.put(data[0], commandStatus);
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
			String nameInFile = line.substring(0, line.indexOf(separator));
			
			for (LabPlayer labPlayer : seenLabPlayers) {
				String playerName = labPlayer.getPlayer().getName();
				
				if (nameInFile.equals(playerName)) {
					LabPlayerCommandStatus commandStatus = labPlayer.getCommandStatus();
					String viewer = String.valueOf(commandStatus.getViewer());
					String ignore = String.valueOf(commandStatus.getIgnore());
					
					writer.write(playerName + separator + viewer + separator + ignore + endLine);
					seenLabPlayers.remove(labPlayer);
					continue Outer;
				}
			}
			
			if (line != endLine)
				writer.write(line + endLine);
		}
		
		for (LabPlayer labPlayer : seenLabPlayers) {
			LabPlayerCommandStatus commandStatus = labPlayer.getCommandStatus();
			
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
			toPlayer = MessageData.PLAYERFOUNDBOSS1.getMessage();
			toOthers = MessageData.PLAYERFOUNDBOSS2.getMessage();
		}
		else {
			toPlayer = MessageData.BOSSFOUNDPLAYER1.getMessage();
			toOthers = MessageData.BOSSFOUNDPLAYER2.getMessage();
		}
		
		toPlayer = toPlayer.replace('&', ChatColor.COLOR_CHAR);
		toOthers = toOthers.replace('&', ChatColor.COLOR_CHAR).replace("{PLAYER}", player.getDisplayName());
		
		for (LabPlayer nearbyLabPlayer : labPlayers) {
			Player nearbyPlayer = nearbyLabPlayer.getPlayer();
			
			if (nearbyPlayer == player)
				player.sendMessage(toPlayer);
			
			else if (Utility.IsNear(nearbyPlayer.getLocation(), entity.getLocation(), 0, 35))
				nearbyPlayer.sendMessage(toOthers);
		}
	}

	public LabPlayer getLabPlayer(Player player) {
		for (LabPlayer labPlayer : labPlayers) {
			if (labPlayer.getPlayer() == player)
				return labPlayer;
		}
		
		return null;
	}
	
	public List<LabPlayer> getLabPlayers() {
		return labPlayers;
	}
}

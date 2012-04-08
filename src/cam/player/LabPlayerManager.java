package cam.player;

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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import cam.Likeaboss;
import cam.Utility;
import cam.config.GlobalConfig.MessageParam;

public class LabPlayerManager {
	
	private static List<LabPlayer> labPlayers = new ArrayList<LabPlayer>();
	private static List<LabPlayer> seenLabPlayers = new ArrayList<LabPlayer>();
	private Pattern pattern = Pattern.compile(StringConst.SEPARATOR.getString());
	
	private enum StringConst {
		FILEPATH {public String getString() {return "plugins/Likeaboss/players.dat";}},
		SEPARATOR {public String getString() {return ":";}},
		ENDLINE {public String getString() {return System.getProperty("line.separator");}};
		
		public abstract String getString();
	}
	
	public void AddOnlinePlayers() {
		Player[] players = Likeaboss.instance.getServer().getOnlinePlayers();
		
		for (Player player : players)
			AddLabPlayer(player);
	}
	
	public void AddLabPlayer(Player player) {
		for (LabPlayer labPlayer : seenLabPlayers) {
			if (labPlayer.getName().equals(player.getName())) {
				labPlayer.setPlayer(player);
				labPlayers.add(labPlayer);
				seenLabPlayers.remove(labPlayer);
				return;
			}
		}
		
		LabPlayer labPlayer = new LabPlayer(player);
		labPlayers.add(labPlayer);
		try {
			LoadPlayerData(labPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void RemoveLabPlayer(Player player) {
		for (LabPlayer labPlayer : labPlayers) {
			if (labPlayer.getName().equals(player.getName())) {
				labPlayers.remove(labPlayer);
				seenLabPlayers.add(labPlayer);
				return;
			}
		}
	}
	
	public void LoadPlayerData(LabPlayer labPlayer) {
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
				labPlayer.AddBossKilled(EntityType.fromId(Integer.valueOf(data[i])), Integer.valueOf(data[i+1]));
			
			break;
		}
		
		scanner.close();
	}
	
	public void SavePlayerData() throws Exception {
		String separator = StringConst.SEPARATOR.getString();
		String endLine = StringConst.ENDLINE.getString();
		
		File file = getFile();
		File tempFile = new File(file.getPath() + ".temp");
		Utility.FileToFile(file, tempFile);
		
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
					
					Map<EntityType, Integer> bossesKilled = labPlayer.getBossesKilled();
					for (Entry<EntityType, Integer> bossKilled : bossesKilled.entrySet())
						writer.write(bossKilled.getKey().getTypeId() + separator + bossKilled.getValue() + separator);
					
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
				
			Map<EntityType, Integer> bossesKilled = labPlayer.getBossesKilled();
			for (Entry<EntityType, Integer> bossKilled : bossesKilled.entrySet())
				writer.write(bossKilled.getKey().getTypeId() + separator + bossKilled.getValue() + separator);
			
			writer.write(endLine);
		}
		
		seenLabPlayers.clear();
		
		writer.close();
	}
	
	public void SendFoundMessage(Player player, boolean isFinder, Entity entity) {
		String toPlayer = null;
		String toOthers = null;
		
		if (isFinder) {
			toPlayer = MessageParam.PLAYER_FOUND_BOSS_1.getMessage();
			toOthers = MessageParam.PLAYER_FOUND_BOSS_2.getMessage();
		}
		else {
			toPlayer = MessageParam.BOSS_FOUND_PLAYER_1.getMessage();
			toOthers = MessageParam.BOSS_FOUND_PLAYER_2.getMessage();
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
	
	public List<LabPlayer> getSeenLabPlayers() {
		return seenLabPlayers;
	}
	
	public List<LabPlayer> getAllLabPlayers() {
		List<LabPlayer> newList = new ArrayList<LabPlayer>();
		newList.addAll(labPlayers);
		newList.addAll(seenLabPlayers);
		return newList;
	}
	
	public File getFile() {
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
	
	public Pattern getPattern() {
		return pattern;
	}
}

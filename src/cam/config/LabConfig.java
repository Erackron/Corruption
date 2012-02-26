package cam.config;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import cam.Likeaboss;
import cam.Utility;
import cam.command.IgnoreCommand;
import cam.drop.Drop;
import cam.drop.Roll;

public class LabConfig {

	private Likeaboss plugin = null;
	private YamlConfiguration configFile = null;
	private List<World> worlds = new ArrayList<World>();
	private Map<World, Set<BossData>> bossesData = new HashMap<World, Set<BossData>>();
	private Map<World, WorldDropData> worldsDropData = new HashMap<World, WorldDropData>();
	
	public LabConfig(Likeaboss plugin) {
		this.plugin = plugin;
	}
	
	public void LoadFiles() {
		try {
			LoadGlobalConfigFile();
			
			worlds = plugin.getServer().getWorlds();
			Iterator<World> it = worlds.iterator();
				
			while (it.hasNext())
				LoadWorldConfigFile(it.next());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void LoadGlobalConfigFile() throws Exception {
		configFile = new YamlConfiguration();
		File file = new File("plugins/Likeaboss/config.yml");
		
		if (!file.exists()) {
			Likeaboss.log.warning("[Likeaboss] Creating default global config file.");
			
			File parentFile = file.getParentFile();
			if (!parentFile.exists())
				parentFile.mkdirs();
			
			file.createNewFile();
			
			InputStream intpuStream = plugin.getResource("cam/config/globalconfig.yml");
			Utility.StreamToFile(intpuStream, file);
		}
		
		LoadConfig(configFile, file);
		
		boolean needSaving = false;
		
		//Command parameters
		String line = "Command.Ignore.Delay";
		if (!configFile.contains(line)) {
			Likeaboss.log.warning("[Likeaboss] Adding '" + line + "' in config file.");
			
			configFile.set(line, 120);
		}
		IgnoreCommand.setDelay(configFile.getInt(line));
		
		//Message parameters
		for (MessageData messageData : MessageData.values()) {
			line = messageData.getLine();
			
			if (!configFile.contains(line)) {
				Likeaboss.log.warning("[Likeaboss] Adding '" + line + "' in config file.");
				
				configFile.set(line, messageData.getMessage());
				needSaving = true;
				continue;
			}
			
			messageData.setMessage(configFile.getString(line));
		}
		
		//Task parameters
		for (TaskData taskData : TaskData.values()) {
			line = taskData.getLine();
				
			if (!configFile.contains(line)) {
				Likeaboss.log.warning("[Likeaboss] Adding '" + line + "' in config file.");
				
				configFile.set(line, taskData.getValue());
				needSaving = true;
				continue;
			}
				
			taskData.setValue(configFile.getDouble(line));
		}
		
		if (needSaving)
			SaveConfig(configFile, file);
	}
	
	public void LoadWorldConfigFile(World world) throws Exception {
		configFile = new YamlConfiguration();
		File folder = new File(world.getName());
		File file = new File("plugins/Likeaboss/" + folder.getName() + "/config.yml");
		
		if (!file.exists()) {
			Likeaboss.log.warning("[Likeaboss] Creating default config file for '" + world.getName() + "'.");
			
			File parentFile = file.getParentFile();
			if (!parentFile.exists())
				parentFile.mkdirs();
			
			file.createNewFile();
			
			InputStream intpuStream = plugin.getResource("cam/config/worldconfig.yml");
			Utility.StreamToFile(intpuStream, file);
		}
		
		LoadConfig(configFile, file);
		
		Set<BossData> tempBossesData = new HashSet<BossData>();
		bossesData.put(world, tempBossesData);
		
		Set<String> entityNames = configFile.getConfigurationSection("Boss").getKeys(false);
		
		for (String entityName : entityNames) {
			EntityType entityType = EntityType.fromName(entityName);
			
			if (entityType == null) {
				Likeaboss.log.warning("[Likeaboss] In '" + world.getName() + "' config file, '" + entityName + "' isn't a valid creature name");
				continue;
			}
			
			BossData bossData = new BossData(entityType);
			tempBossesData.add(bossData);
			
			String bossEntry = "Boss." + entityName;
			Map<String, Object> datas = configFile.getConfigurationSection(bossEntry).getValues(false);
			
			Boss:
			for (Entry<String, Object> data : datas.entrySet()) {
				String dataEntry = bossEntry + "." + data.getKey();
				String dataType = data.getKey();
				
				//Spawn data
				if (dataType.equalsIgnoreCase("Spawn")) {
					String rawValue = data.getValue().toString();
					
					for (int i = 0 ; i < rawValue.length() ; i++) {
						char c = rawValue.charAt(i);
						
						if (!Character.isDigit(c) && c != '.' && c != ' ') {
							Likeaboss.log.warning("[Likeaboss] Invalid values for '" + dataEntry + "' in '" + world.getName() + "' config file");
							continue Boss;
						}
					}
					
					String[] values = rawValue.split(" ");
					
					if (values.length < 3) {
						Likeaboss.log.warning("[Likeaboss] Missing values for '" + dataEntry + "' in '" + world.getName() + "' config file");
						continue;
					}
					
					bossData.setSpawnData(Double.valueOf(values[0]), Double.valueOf(values[1]), Integer.valueOf(values[2]));
				}
				
				//Stats data
				else if (dataType.equalsIgnoreCase("Stats")) {
					String rawValue = data.getValue().toString();
					
					for (int i = 0 ; i < rawValue.length() ; i++) {
						char c = rawValue.charAt(i);
						
						if (!Character.isDigit(c) && c != '.' && c != ' ') {
							Likeaboss.log.warning("[Likeaboss] Invalid values for '" + dataEntry + "' in '" + world.getName() + "' config file");
							continue Boss;
						}
					}
					
					String[] values = rawValue.split(" ");
					
					if (values.length < 3) {
						Likeaboss.log.warning("[Likeaboss] Missing values for '" + dataEntry + "' in '" + world.getName() + "' config file");
						continue;
					}
					
					bossData.setStatData(Double.valueOf(values[0]), Double.valueOf(values[1]), Double.valueOf(values[2]));
				}
				
				//Drops data
				else if (dataType.equalsIgnoreCase("Drop")) {
					Set<String> rolls = configFile.getConfigurationSection(dataEntry).getKeys(false);
					
					for (String roll : rolls) {
						String rollEntry = dataEntry + "." + roll;
						
						Roll newRoll = new Roll();
						bossData.AddRoll(newRoll);
						
						Map<String, Object> drops = configFile.getConfigurationSection(rollEntry).getValues(false);
						
						Drop:
						for (Entry<String, Object> drop : drops.entrySet()) {
							String dropEntry = rollEntry + "." + drop.getKey();
							String rawValue = drop.getValue().toString();
							
							for (int i = 0 ; i < rawValue.length() ; i++) {
								char c = rawValue.charAt(i);
								
								if (!Character.isDigit(c) && c != '.' && c != ':' && c != ' ') {
									
									Likeaboss.log.warning("[Likeaboss] Invalid values for '" + dropEntry + "' in '" + world.getName() + "' config file");
									continue Drop;
								}
							}
							
							String[] values = rawValue.split(" ");
							
							if (values.length < 4) {
								Likeaboss.log.warning("[Likeaboss] Missing values for '" + dropEntry + "' in '" + world.getName() + "' config file");
								continue;
							}
							
							Material material = null;
							short metaData = 0;
							
							if (values[0].contains(":")) {
								String[] tempData = values[0].split(":");
								material = Material.getMaterial(Integer.valueOf(tempData[0]));
								metaData = Short.valueOf(tempData[1]);
							}
							else
								material = Material.getMaterial(Integer.valueOf(values[0]));
							
							Drop newDrop = new Drop(material, metaData, Double.valueOf(values[1]), Integer.valueOf(values[2]), Integer.valueOf(values[3]));
							newRoll.AddDrop(newDrop);
						}
					}
				}
			}
		}
		
		WorldDropData worldDropData = new WorldDropData();
		worldsDropData.put(world, worldDropData);
		
		Set<String> rolls = configFile.getConfigurationSection("Drop").getKeys(false);
		
		for (String roll : rolls) {
		String rollEntry = "Drop." + roll;
		
		Roll newRoll = new Roll();
		worldDropData.AddRoll(newRoll);
		
		Map<String, Object> drops = configFile.getConfigurationSection(rollEntry).getValues(false);
		
		Drop:
		for (Entry<String, Object> drop : drops.entrySet()) {
			String dropEntry = rollEntry + "." + drop.getKey();
			String rawValue = drop.getValue().toString();
			
			for (int i = 0 ; i < rawValue.length() ; i++) {
				char c = rawValue.charAt(i);
			
				if (!Character.isDigit(c) && c != '.' && c != ':' && c != ' ') {
					Likeaboss.log.warning("[Likeaboss] Invalid values for '" + dropEntry + "' in '" + world.getName() + "' config file");
					continue Drop;
				}
			}
			
			String[] values = rawValue.split(" ");
			
			if (values.length < 4) {
				Likeaboss.log.warning("[Likeaboss] Missing values for '" + dropEntry + "' in '" + world.getName() + "' config file");
				continue;
			}
			
			Material material = null;
			short metaData = 0;
			
			if (values[0].contains(":")) {
				String[] tempData = values[0].split(":");
				material = Material.getMaterial(Integer.valueOf(tempData[0]));
				metaData = Short.valueOf(tempData[1]);
			}
			else
				material = Material.getMaterial(Integer.valueOf(values[0]));
				
				Drop newDrop = new Drop(material, metaData, Double.valueOf(values[1]), Integer.valueOf(values[2]), Integer.valueOf(values[3]));
				newRoll.AddDrop(newDrop);
			}
		}
	}
	
	private void LoadConfig(YamlConfiguration configFile, File file) {
		try {
			configFile.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void SaveConfig(YamlConfiguration configFile, File file) {
		try {
			configFile.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void RemoveWorldData(World world) {
		bossesData.remove(world);
		worldsDropData.remove(world);
	}
	
	public BossData getBossData(LivingEntity livingEntity) {
		World world = livingEntity.getWorld();
		Set<BossData> set = bossesData.get(world);
		
		if (set == null) {
			try {
				LoadWorldConfigFile(world);
			} catch (Exception e) {
				e.printStackTrace();
			}
			set = bossesData.get(world);
		}
		
		for (BossData bossData : set) {
			if (bossData.getEntityType() == livingEntity.getType())
				return bossData;
		}
		
		return null;
	}
	
	public WorldDropData getWorldDropData(World world) {
		WorldDropData worldDropData = worldsDropData.get(world);
		
		if (worldDropData == null) {
			try {
				LoadWorldConfigFile(world);
			} catch (Exception e) {
				e.printStackTrace();
			}
			worldDropData = worldsDropData.get(world);
		}
		
		return worldDropData;
	}
}

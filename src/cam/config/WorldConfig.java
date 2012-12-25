package cam.config;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import cam.Likeaboss;
import cam.ability.Ability;
import cam.drop.Drop;
import cam.drop.Roll;
import cam.entity.BossData;
import cam.world.WorldData;

public abstract class WorldConfig extends BaseConfig {
	private static Map<World, WorldData> worldsData = new HashMap<World, WorldData>();
		
	public static void Load(World world) {
		String worldName = world.getName();
		File file = LoadFile("plugins/Likeaboss/Worlds/" + worldName + ".yml", "cam/config/world.yml");
		
		if (file == null)
			return;
		
		YamlConfiguration yamlConfig = LoadConfig(file);
		WorldData worldData = new WorldData();
		
		LoadBosses(worldData, yamlConfig.getStringList("Boss"), worldName);
		LoadAbilities(worldData, yamlConfig.getStringList("Ability"), worldName);
		LoadLoots(worldData, yamlConfig.getConfigurationSection("Loot"), worldName);
		worldsData.put(world, worldData);
	}
	
	private static void LoadBosses(WorldData worldData, List<String> bossNames, String worldName) {
		Map<String, BossData> bossesData = BossConfig.getBossesData();
		
		for (String bossName : bossNames) {
			if (!bossesData.containsKey(bossName)) {
				Likeaboss.l.warning("[Likeaboss] '" + bossName + "' in '" + worldName + "' + config file isn't a valid boss.");
				continue;
			}
			
			worldData.AddBossData(bossesData.get(bossName));
		}
	}
	
	private static void LoadAbilities(WorldData worldData, List<String> abilityNames, String worldName) {
		Map<String, Ability> abilities = AbilityConfig.getAbilities();
		
		for (String abilityName : abilityNames) {
			if (!abilities.containsKey(abilityName)) {
				Likeaboss.l.warning("[Likeaboss] '" + abilityName + "' in '" + worldName + "' + config file isn't a valid ability.");
				continue;
			}
			
			worldData.AddAbility(abilities.get(abilityName));
		}
	}
	
	private static void LoadLoots(WorldData worldData, ConfigurationSection lootSection, String worldName) {
		if (lootSection == null) {
			Likeaboss.l.warning("[Likeaboss] 'Loot' in '" + worldName + "' config file is invalid.");
			return;
		}
		
		Set<String> rollStrings = lootSection.getKeys(false);
		
		for (String rollString : rollStrings) {
			ConfigurationSection rollSection = lootSection.getConfigurationSection(rollString);
			
			if (rollSection == null) {
				Likeaboss.l.warning("[Likeaboss] 'Loot." + rollString + "' in '" + worldName + "' config file is invalid.");
				continue;
			}
			
			Roll roll = new Roll();
			Map<String, Object> drops = rollSection.getValues(false);
			
			for (Entry<String, Object> dropEntry : drops.entrySet()) {
				String dropString = dropEntry.getValue().toString();
				
//				if (!IsValidString(rawValue)) {
//					Likeaboss.logger.warning("[Likeaboss] Invalid values for '" + dropEntry + "' in '" + world.getName() + "' config file");
//					continue;
//				}
				
				String[] dropValues = dropString.split(" ");
				
				if (dropValues.length < 4) {
					Likeaboss.l.warning("[Likeaboss] Missing values for '" + "Loot." + rollString + "." + dropEntry.getKey() + "' in '" + worldName + "' config file.");
					continue;
				}
				
				Material material = null;
				short metaData = 0;
				
				if (dropValues[0].contains(":")) {
					String[] tempData = dropValues[0].split(":");
					material = Material.getMaterial(Integer.valueOf(tempData[0]));
					metaData = Short.valueOf(tempData[1]);
				}
				else
					material = Material.getMaterial(Integer.valueOf(dropValues[0]));
				
				Drop drop = new Drop(material, metaData, Double.valueOf(dropValues[1]), Integer.valueOf(dropValues[2]), Integer.valueOf(dropValues[3]));
				
				roll.AddDrop(drop);
			}
			
			worldData.AddRoll(roll);
		}
	}
	
	public static WorldData getWorldData(World world) {
		return worldsData.get(world);
	}
	
	public static void Remove(World world) {
		worldsData.remove(world);
	}
}

package com.mcdr.corruption.config;

import com.mcdr.corruption.ability.Ability;
import com.mcdr.corruption.drop.Drop;
import com.mcdr.corruption.drop.Roll;
import com.mcdr.corruption.entity.CorItem;
import com.mcdr.corruption.entity.Spawner;
import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.util.CorLogger;
import com.mcdr.corruption.world.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class WorldConfig extends BaseConfig {
    private static Map<World, WorldData> worldsData = new HashMap<World, WorldData>();

    public static void Load(World world) {
        String worldName = world.getName();
        File file = new File(DATAFOLDER + SEPERATOR + "Worlds", worldName + ".yml");

        if (!file.exists())
            copyResource(file, "com/mcdr/corruption/config/world.yml");

        YamlConfiguration yamlConfig = loadConfig(file);
        WorldData worldData = new WorldData();

        LoadBosses(worldData, yamlConfig.getStringList("Boss"), worldName);
        LoadAbilities(worldData, yamlConfig.getStringList("Ability"), worldName);
        LoadLoots(worldData, yamlConfig.getConfigurationSection("Loot"), worldName);
        LoadSpawners(yamlConfig.getConfigurationSection("Spawners"), worldName);
        worldsData.put(world, worldData);
    }

    private static void LoadBosses(WorldData worldData, List<String> bossNames, String worldName) {
        Map<String, BossData> bossesData = BossConfig.getBossesData();

        for (String bossName : bossNames) {
            if (!bossesData.containsKey(bossName)) {
                CorLogger.w("'" + bossName + "' in '" + worldName + "' config file isn't a valid boss.");
                continue;
            }

            worldData.addBossData(bossesData.get(bossName));
        }
    }

    private static void LoadAbilities(WorldData worldData, List<String> abilityNames, String worldName) {
        Map<String, Ability> abilities = AbilityConfig.getAbilities();

        for (String abilityName : abilityNames) {
            if (!abilities.containsKey(abilityName)) {
                CorLogger.w("'" + abilityName + "' in '" + worldName + " config file isn't a valid ability.");
                continue;
            }

            worldData.addAbility(abilities.get(abilityName));
        }
    }

    private static void LoadSpawners(ConfigurationSection spawners, String worldName) {
        Map<String, BossData> bossesData = BossConfig.getBossesData();
        if (spawners == null)
            return;

        for (String spawnerName : spawners.getKeys(false)) {
            int id = spawners.getInt(spawnerName + ".id");
            if (id == 0) {
                CorLogger.w("'Spawners." + spawnerName + ".id' in '" + worldName + " config file is invalid.");
                continue;
            }
            boolean enabled = spawners.getBoolean(spawnerName + ".enabled", true);
            int x = spawners.getInt(spawnerName + ".X");
            int y = spawners.getInt(spawnerName + ".Y");
            int z = spawners.getInt(spawnerName + ".Z");
            int radius = spawners.getInt(spawnerName + ".Radius");
            if (radius <= 0 || radius > 30) {
                CorLogger.w("'Spawners." + spawnerName + ".Radius' in '" + worldName + " config file should be between 1 and 30.");
                continue;
            }
            int spawnInterval = spawners.getInt(spawnerName + ".SpawnInterval");
            if (spawnInterval <= 0) {
                CorLogger.w("'Spawners." + spawnerName + ".SpawnInterval' in '" + worldName + " config file should be > 0.");
                continue;
            }
            int spawnCap = spawners.getInt(spawnerName + ".SpawnCap");
            if (spawnInterval <= 0) {
                CorLogger.w("'Spawners." + spawnerName + ".SpawnCap' in '" + worldName + " config file should be > 0.");
                continue;
            }

            ConfigurationSection bosses = spawners.getConfigurationSection(spawnerName + ".Bosses");
            if (bosses == null) {
                CorLogger.i("'Spawners." + spawnerName + "' in '" + worldName + "' doesn't have any Bosses set to spawn. (Set value of Bosses to {} for this spawner to suppress this message)");
                continue;
            }
            HashMap<BossData, Integer> bossMap = new HashMap<BossData, Integer>();
            BossData bossData;
            for (String boss : bosses.getKeys(false)) {
                if (!bossesData.containsKey(boss)) {
                    CorLogger.w("'" + boss + "' in '" + worldName + "' config file isn't a valid boss.");
                    continue;
                }
                bossData = bossesData.get(boss);
                bossMap.put(bossData, bosses.getInt(boss));
            }
            GlobalConfig.addSpawner(new Spawner(spawnerName, id, x, y, z, radius, Bukkit.getWorld(worldName), enabled, spawnInterval, spawnCap, bossMap));
        }


    }

    private static void LoadLoots(WorldData worldData, ConfigurationSection lootSection, String worldName) {
        if (lootSection == null) {
            CorLogger.w("'Loot' in '" + worldName + "' config file is invalid.");
            return;
        }

        Set<String> rollStrings = lootSection.getKeys(false);

        for (String rollString : rollStrings) {
            ConfigurationSection rollSection = lootSection.getConfigurationSection(rollString);

            if (rollSection == null) {
                CorLogger.w("'Loot." + rollString + "' in '" + worldName + "' config file is invalid.");
                continue;
            }

            Roll roll = new Roll();
            Set<String> drops = rollSection.getKeys(false);
            for(String drop:drops){
                if(!rollSection.isConfigurationSection(drop)){
                    CorLogger.w("'"+rollSection.getCurrentPath()+"."+drop+"' in the " + worldName + " config file is invalid");
                    continue;
                }
                CorItem item = ItemConfig.items.get(drop);

                int probability = rollSection.getConfigurationSection(drop).getInt("Probability");
                int minQuantity = rollSection.getConfigurationSection(drop).getInt("MinQuantity");
                int maxQuantity = rollSection.getConfigurationSection(drop).getInt("MaxQuantity");

                if(item==null){
                    CorLogger.w("'"+rollSection.getCurrentPath()+"."+drop+"' in the " + worldName + " config file is invalid");
                    continue;
                }

                if(probability<0||probability>100){
                    CorLogger.w("'"+rollSection.getCurrentPath()+"."+drop+".Probability' in the " + worldName + " config file is invalid");
                    continue;
                }

                if(minQuantity<0){
                    CorLogger.w("'"+rollSection.getCurrentPath()+"."+drop+".MinQuantity' in the " + worldName + " config file is invalid");
                    continue;
                }
                if(maxQuantity<0){
                    CorLogger.w("'"+rollSection.getCurrentPath()+"."+drop+".MaxQuantity' in the " + worldName + " config file is invalid");
                    continue;
                }
                if(minQuantity>maxQuantity){
                    CorLogger.w("'"+rollSection.getCurrentPath()+"."+drop+".MaxQuantity' in the " + worldName + " config file is invalid");
                    continue;
                }

                roll.addDrop(new Drop(item, probability, minQuantity, maxQuantity));
            }

            worldData.addRoll(roll);
        }
    }

    public static void resetWorldsData() {
        worldsData = new HashMap<World, WorldData>();
    }

    public static WorldData getWorldData(World world) {
        return worldsData.get(world);
    }

    public static void remove(World world) {
        worldsData.remove(world);
    }

    public static void updateSpawner(World world, Spawner spawner, boolean enabled) {
        String worldName = world.getName();
        File file = new File(DATAFOLDER + SEPERATOR + "Worlds", worldName + ".yml");

        if (!file.exists())
            return;

        YamlConfiguration yamlConfig = loadConfig(file);
        yamlConfig.set("Spawners." + spawner.getName() + ".enabled", enabled);
        saveConfig(yamlConfig, worldName + ".yml", true);
    }

    public static void updateSpawnerRemoveBoss(World world, Spawner spawner, String bossName) {
        updateSpawner(world, spawner, bossName, 0, false);
    }

    public static void updateSpawner(World world, Spawner spawner, String bossName, int chance) {
        updateSpawner(world, spawner, bossName, chance, true);
    }

    public static void updateSpawner(World world, Spawner spawner, String bossName, int chance, boolean addNotRemove) {
        String worldName = world.getName();
        File file = new File(DATAFOLDER + SEPERATOR + "Worlds", worldName + ".yml");

        if (!file.exists())
            return;

        YamlConfiguration yamlConfig = loadConfig(file);
        yamlConfig.set("Spawners." + spawner.getName() + ".Bosses." + bossName, addNotRemove ? chance : null);
        saveConfig(yamlConfig, worldName + ".yml", true);
    }

    public static void updateSpawnerProperty(World world, Spawner spawner, String property, Object value) {
        String worldName = world.getName();
        File file = new File(DATAFOLDER + SEPERATOR + "Worlds", worldName + ".yml");

        if (!file.exists())
            return;

        YamlConfiguration yamlConfig = loadConfig(file);
        ConfigurationSection spawnerSection = yamlConfig.getConfigurationSection("Spawners." + spawner.getName());

        if (spawnerSection == null)
            return;

        if (property.equals("name")) {
            Map<String, Object> values = spawnerSection.getValues(true);
            yamlConfig.set("Spawners." + spawner.getName(), null);
            yamlConfig.set("Spawners." + value.toString(), values);
        } else if (property.equals("world")) {
            File file2 = new File(DATAFOLDER + SEPERATOR + "Worlds", value + ".yml");
            if (!file2.exists())
                return;
            YamlConfiguration yamlConfig2 = loadConfig(file2);
            yamlConfig2.createSection("Spawners." + spawner.getName(), spawnerSection.getValues(true));
            yamlConfig.set("Spawners." + spawner.getName(), null);
            saveConfig(yamlConfig2, value + ".yml", true);
        } else {
            if (spawnerSection.isSet(property))
                spawnerSection.set(property, value);
        }

        saveConfig(yamlConfig, worldName + ".yml", true);
    }

    public static void addSpawner(World world, Spawner spawner) {
        String worldName = world.getName();
        File file = new File(DATAFOLDER + SEPERATOR + "Worlds", worldName + ".yml");

        if (!file.exists())
            return;

        YamlConfiguration yamlConfig = loadConfig(file);
        ConfigurationSection spawnerSection = yamlConfig.createSection("Spawners." + spawner.getName());
        Map<String, Object> info = spawner.getInfo();
        spawnerSection.set("enabled", info.get("enabled"));
        spawnerSection.set("id", info.get("id"));
        spawnerSection.set("X", info.get("x"));
        spawnerSection.set("Y", info.get("y"));
        spawnerSection.set("Z", info.get("z"));
        spawnerSection.set("Radius", info.get("radius"));
        spawnerSection.set("SpawnInterval", (int) (spawner.getSpawnInterval() / 20));
        spawnerSection.set("SpawnCap", info.get("spawncap"));
        spawnerSection = spawnerSection.createSection("Bosses");
        for (Map.Entry s : ((Map<?, ?>) info.get("spawnable")).entrySet()) {
            if (s.getKey() instanceof BossData && s.getValue() instanceof Integer) {
                spawnerSection.set(((BossData) s.getKey()).getName(), s.getValue());
            }
        }
        saveConfig(yamlConfig, worldName + ".yml", true);
    }

    public static void removeSpawner(World world, Spawner spawner) {
        String worldName = world.getName();
        File file = new File(DATAFOLDER + SEPERATOR + "Worlds", worldName + ".yml");

        if (!file.exists())
            return;

        YamlConfiguration yamlConfig = loadConfig(file);
        yamlConfig.set("Spawners." + spawner.getName(), null);
        saveConfig(yamlConfig, worldName + ".yml", true);
    }
}

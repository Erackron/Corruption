package com.mcdr.corruption.config;

import com.mcdr.corruption.ability.Ability;
import com.mcdr.corruption.drop.Drop;
import com.mcdr.corruption.drop.Roll;
import com.mcdr.corruption.entity.CorItem;
import com.mcdr.corruption.entity.EquipmentSet;
import com.mcdr.corruption.entity.data.*;
import com.mcdr.corruption.entity.data.BossData.BossImmunity;
import com.mcdr.corruption.logger.CorLogger;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.*;


public class BossConfig extends BaseConfig {
    private static Map<String, BossData> bossesData;
    private static Set<EntityType> usedBossEntityTypes;

    public static void Load() {
        File file = new File(DATAFOLDER, "bosses.yml");

        if (!file.exists())
            copyResource(file, "config/bosses.yml");

        LoadBosses(loadConfig(file));
    }

    private static void LoadBosses(YamlConfiguration yamlConfig) {
        Set<String> bossNames = yamlConfig.getKeys(false);
        bossNames.remove("ConfigVersion");
        bossesData = new HashMap<String, BossData>();
        usedBossEntityTypes = new HashSet<EntityType>();
        for (String bossName : bossNames) {
            ConfigurationSection configurationSection = yamlConfig.getConfigurationSection(bossName);

            if (configurationSection == null) {
                CorLogger.warning("'" + bossName + "' in bosses config file is invalid.");
                continue;
            }

            String entityTypeString = yamlConfig.getString(bossName + ".EntityType");
            @SuppressWarnings("deprecation")
            EntityType entityType = EntityType.fromName(entityTypeString); //TODO Find alternative for EntityType.fromName(String name)

            if (entityType == null) {
                CorLogger.warning("'" + entityTypeString + "' in bosses config file isn't a valid EntityType.");
                continue;
            }

            usedBossEntityTypes.add(entityType);

            BossData bossData;
            switch (entityType) {
                case ZOMBIE:
                    boolean isBaby = yamlConfig.getBoolean(bossName + ".Baby"), isVillager = yamlConfig.getBoolean(bossName + ".Villager");
                    bossData = new ZombieBossData(bossName, entityType, isBaby, isVillager);
                    break;
                case PIG_ZOMBIE:
                    boolean isPigBaby = yamlConfig.getBoolean(bossName + ".Baby"), isAngry = !yamlConfig.isSet(bossName + ".Aggressive") || yamlConfig.getBoolean(bossName + ".Aggressive");
                    bossData = new PigZombieBossData(bossName, entityType, isPigBaby, isAngry);
                    break;
                case SKELETON:
                    boolean isWitherSkeleton = yamlConfig.getBoolean(bossName + ".WitherSkeleton");
                    bossData = new SkeletonBossData(bossName, entityType, isWitherSkeleton);
                    break;
                case WITHER:
                    int regenPerSecond = yamlConfig.getInt(bossName + ".HealthRegenPerSecond");
                    bossData = new WitherBossData(bossName, entityType, regenPerSecond > 0 ? regenPerSecond : 1);
                    break;
                case SLIME:
                case MAGMA_CUBE:
                    int minsize = yamlConfig.isSet(bossName + ".MinimumSize") ? yamlConfig.getInt(bossName + ".MinimumSize") : 2;
                    int maxsize = yamlConfig.isSet(bossName + ".MaximumSize") ? yamlConfig.getInt(bossName + ".MaximumSize") : 4;
                    bossData = new SlimeBossData(bossName, entityType, minsize, maxsize);
                    break;
                case GHAST:
                    boolean immune = !yamlConfig.isSet(bossName + ".ReturnToSenderImmune") || yamlConfig.getBoolean(bossName + ".ReturnToSenderImmune");
                    bossData = new GhastBossData(bossName, entityType, immune);
                    break;
                default:
                    bossData = new BossData(bossName, entityType);
            }

            if (!LoadSpawnValues(bossData, yamlConfig.getConfigurationSection(bossName + ".Spawn"), bossName))
                continue;
            if (!LoadStats(bossData, yamlConfig.getConfigurationSection(bossName + ".Stats"), bossName))
                continue;
            LoadAbilities(bossData, yamlConfig.getStringList(bossName + ".Ability"), bossName);
            LoadLoots(bossData, yamlConfig.getConfigurationSection(bossName + ".Loot"), bossName);
            LoadEquipment(bossData, yamlConfig.getConfigurationSection(bossName));
            LoadImmunities(bossData, yamlConfig.getConfigurationSection(bossName + ".Immunity"), bossName);
            LoadMCMMOXPBonus(bossData, yamlConfig.getInt(bossName + ".mcMMOXPBonus"));
            LoadHeroesXPBonus(bossData, yamlConfig.getDouble(bossName + ".HeroesKillingExperience"));
            LoadBiomes(bossData, yamlConfig.getStringList(bossName + ".Biomes"), bossName);

            String[] bossNameS = bossName.split("_");
            bossName = bossNameS[0];
            for (int i = 1; i < bossNameS.length; i++) {
                bossName += " " + bossNameS[i];
            }
            bossesData.put(bossName, bossData);
        }
    }

    private static boolean LoadSpawnValues(BossData bossData, ConfigurationSection spawnSection, String bossName) {
        if (spawnSection == null) {
            CorLogger.warning("'" + bossName + ".Spawn' in bosses config file is missing.");
            return false;
        }

        if (!(spawnSection.isSet("Probability") && spawnSection.isSet("SpawnerProbability"))) {
            CorLogger.warning("'" + bossName + ".Spawn' in bosses config file is invalid.");
            return false;
        }

        double minheight, maxheight;
        if (spawnSection.isSet("MaxSpawnHeight"))
            maxheight = spawnSection.getDouble("MaxSpawnHeight");
        else
            maxheight = 256;

        if (spawnSection.isSet("MinSpawnHeight"))
            minheight = spawnSection.getDouble("MinSpawnHeight");
        else
            minheight = 0;

        bossData.setSpawnData(spawnSection.getDouble("Probability"), spawnSection.getDouble("SpawnerProbability"), minheight, maxheight);
        return true;
    }

    private static boolean LoadStats(BossData bossData, ConfigurationSection statsSection, String bossName) {

        if (!(statsSection.isSet("Health") && statsSection.isSet("Damage") && statsSection.isSet("Experience"))) {
            CorLogger.info("Missing values in '" + bossName + ".Stats'");
            return false;
        }

        bossData.setStatData(statsSection.getDouble("Health"), statsSection.getDouble("Damage"), statsSection.getDouble("Experience"));
        return true;
    }

    private static void LoadAbilities(BossData bossData, List<String> abilityNames, String bossName) {
        Map<String, Ability> abilities = AbilityConfig.getAbilities();

        for (String abilityName : abilityNames) {
            if (!abilities.containsKey(abilityName)) {
                CorLogger.warning("'" + bossName + ".Ability." + abilityName + "' in bosses config file isn't a valid ability.");
                continue;
            }

            bossData.AddAbility(abilities.get(abilityName));
        }
    }

    private static void LoadLoots(BossData bossData, ConfigurationSection lootSection, String bossName) {
        if (lootSection == null) {
            CorLogger.warning("'" + bossName + ".Loot" + "' in bosses config file is invalid.");
            return;
        }

        Set<String> rollStrings = lootSection.getKeys(false);

        for (String rollString : rollStrings) {
            ConfigurationSection rollSection = lootSection.getConfigurationSection(rollString);

            if (rollSection == null) {
                CorLogger.warning("'" + bossName + ".Loot." + rollString + "' in bosses config file is invalid.");
                continue;
            }

            Roll roll = new Roll();

            Set<String> drops = rollSection.getKeys(false);
            for (String drop : drops) {
                if (!rollSection.isConfigurationSection(drop)) {
                    CorLogger.warning("'" + rollSection.getCurrentPath() + "." + drop + "' in the boss config file is invalid");
                    continue;
                }
                CorItem item = ItemConfig.items.get(drop);

                int probability = rollSection.getConfigurationSection(drop).getInt("Probability");
                int minQuantity = rollSection.getConfigurationSection(drop).getInt("MinQuantity");
                int maxQuantity = rollSection.getConfigurationSection(drop).getInt("MaxQuantity");

                if (item == null) {
                    CorLogger.warning("'" + rollSection.getCurrentPath() + "." + drop + "' in the boss config file is invalid");
                    continue;
                }

                if (probability < 0 || probability > 100) {
                    CorLogger.warning("'" + rollSection.getCurrentPath() + "." + drop + ".Probability' in the boss config file is invalid");
                    continue;
                }

                if (minQuantity < 0) {
                    CorLogger.warning("'" + rollSection.getCurrentPath() + "." + drop + ".MinQuantity' in the boss config file is invalid");
                    continue;
                }
                if (maxQuantity < 0) {
                    CorLogger.warning("'" + rollSection.getCurrentPath() + "." + drop + ".MaxQuantity' in the boss config file is invalid");
                    continue;
                }
                if (minQuantity > maxQuantity) {
                    CorLogger.warning("'" + rollSection.getCurrentPath() + "." + drop + ".MaxQuantity' in the boss config file is invalid");
                    continue;
                }

                roll.addDrop(new Drop(item, probability, minQuantity, maxQuantity));
            }
            bossData.AddRoll(roll);
        }
    }

    public static void LoadEquipment(BossData bossData, ConfigurationSection section) {
        String equipmentSetName = section.getString("EquipmentSet");
        if (equipmentSetName == null) {
            bossData.setEquipment(new EquipmentSet());
            return;
        }
        if (EquipmentConfig.equipmentSets.containsKey(equipmentSetName))
            bossData.setEquipment(EquipmentConfig.equipmentSets.get(equipmentSetName));
        else
            CorLogger.warning("'" + section.getName() + ".EquipmentSet' in bosses config file is invalid or doesn't exist.");
    }

    public static void LoadImmunities(BossData bossData, ConfigurationSection section, String bossName) {
        if (section == null) {
            CorLogger.warning("'" + bossName + ".Immunities" + "' in bosses config file doesn't exist.");
            return;
        }

        for (BossImmunity immunities : BossImmunity.values()) {
            if (immunities.getNode() != null) {
                boolean immunityNode;
                immunityNode = section.isSet(immunities.getNode()) && section.getBoolean(immunities.getNode());
                bossData.setImmunity(immunities.getNode(), immunityNode);
            }
        }

    }

    public static void LoadMCMMOXPBonus(BossData bossData, int xp) {
        bossData.setMCMMOXPBonus(xp);
    }

    public static void LoadHeroesXPBonus(BossData bossData, double xp) {
        bossData.setHeroesXPBonus(xp);
    }

    public static void LoadBiomes(BossData bossData, List<String> biomeStrings, String bossName) {
        if (GlobalConfig.BossParam.ENABLE_BIOMES.getValue() && biomeStrings == null) {
            CorLogger.warning("'" + bossName + ".Biomes' does not exist");
            return;
        }

        List<Biome> biomes = new ArrayList<Biome>();
        for (String biome : biomeStrings) {
            try {
                biomes.add(Biome.valueOf(biome.toUpperCase()));
            } catch (IllegalArgumentException e) {
                CorLogger.warning("'" + bossName + ".Biomes." + biome + "' does not exist");
            }
        }

        bossData.setBiomes(biomes);
    }

    public static Map<String, BossData> getBossesData() {
        return bossesData;
    }

    public static Set<EntityType> getEntityTypesUsed() {
        return usedBossEntityTypes;
    }
}


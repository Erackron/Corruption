package com.mcdr.corruption.config;

import com.mcdr.corruption.ability.*;
import com.mcdr.corruption.ability.Ability.AbilityType;
import com.mcdr.corruption.ability.Ability.ActivationCondition;
import com.mcdr.corruption.util.CorLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.*;

import static com.mcdr.corruption.ability.Summon.*;

public abstract class AbilityConfig extends BaseConfig {
    private static Map<String, Ability> abilities;

    public static void Load() {
        File file = new File(DATAFOLDER, "abilities.yml");

        if (!file.exists())
            copyResource(file, "com/mcdr/corruption/config/abilities.yml");

        LoadAbilities(loadConfig(file));

    }

    private static void LoadAbilities(YamlConfiguration yamlConfig) {
        Set<String> abilityNames = yamlConfig.getKeys(false);
        abilityNames.remove("ConfigVersion");
        abilities = new HashMap<String, Ability>();
        for (String abilityName : abilityNames) {
            String node = abilityName;
            Map<String, Object> abilityEntries = yamlConfig.getConfigurationSection(node).getValues(true);
            String entryKey = "Type";
            node += "." + entryKey;

            if (!abilityEntries.containsKey(entryKey)) {
                CorLogger.warning("'" + node + "' in abilities config file is missing.");
                continue;
            }

            String entryValue = yamlConfig.getString(node);
            AbilityType abilityType = AbilityType.fromString(entryValue);

            Ability ability = null;

            switch (abilityType) {
                case ARMORPIERCE:
                    ability = new ArmorPierce();

                    entryKey = "Value";
                    if (abilityEntries.containsKey(entryKey))
                        ((ArmorPierce) ability).setValue((Double) abilityEntries.get(entryKey));
                    break;

                case FIREPUNCH:
                    ability = new FirePunch();

                    entryKey = "Ticks";
                    if (abilityEntries.containsKey(entryKey))
                        ((FirePunch) ability).setTicks((Integer) abilityEntries.get(entryKey));
                    break;

                case KNOCKBACK:
                    ability = new Knockback();

                    entryKey = "VerticalCoef";
                    if (abilityEntries.containsKey(entryKey))
                        ((Knockback) ability).setVerticalCoef((Double) abilityEntries.get(entryKey));

                    entryKey = "HorizontalCoef";
                    if (abilityEntries.containsKey(entryKey))
                        ((Knockback) ability).setHorizontalCoef((Double) abilityEntries.get(entryKey));
                    break;

                case POTION:
                    ability = new Potion();

                    entryKey = "Target";
                    ((Potion) ability).setTarget((abilityEntries.containsKey(entryKey)) ? (String) abilityEntries.get(entryKey) : "other");

                    entryKey = "Amplifier";
                    if (abilityEntries.containsKey(entryKey))
                        ((Potion) ability).setAmplifier((Integer) abilityEntries.get(entryKey));

                    entryKey = "Duration";
                    if (abilityEntries.containsKey(entryKey))
                        ((Potion) ability).setDuration(((Double) abilityEntries.get(entryKey)).intValue() * 20);

                    entryKey = "Effect";
                    if (abilityEntries.containsKey(entryKey))
                        ((Potion) ability).setEffect((String) abilityEntries.get(entryKey));
                    break;

                case BOMB:
                    ability = new Bomb();

                    entryKey = "Fuse";
                    if (abilityEntries.containsKey(entryKey))
                        ((Bomb) ability).setFuseTicks((Integer) abilityEntries.get(entryKey));

                    entryKey = "DestroyWorld";
                    if (abilityEntries.containsKey(entryKey))
                        ((Bomb) ability).setDestroyWorld((Boolean) abilityEntries.get(entryKey));

                    entryKey = "Fire";
                    if (abilityEntries.containsKey(entryKey))
                        ((Bomb) ability).setFire((Boolean) abilityEntries.get(entryKey));

                    entryKey = "ExplosionRadius";
                    if (abilityEntries.containsKey(entryKey))
                        ((Bomb) ability).setRadius((float) ((Integer) abilityEntries.get(entryKey)));
                    break;

                case LIGHTNINGAURA:
                    ability = new LightningAura();

                    entryKey = "Damage";
                    if (abilityEntries.containsKey(entryKey))
                        ((LightningAura) ability).setDamage((Integer) abilityEntries.get(entryKey));

                    entryKey = "Fire";
                    if (abilityEntries.containsKey(entryKey))
                        ((LightningAura) ability).setFire((Boolean) abilityEntries.get(entryKey));

                    entryKey = "ArmorPierce";
                    if (abilityEntries.containsKey(entryKey))
                        ((LightningAura) ability).setArmorPierce((Boolean) abilityEntries.get(entryKey));

                    break;
                case TELEPORT:
                    ability = new Teleport();

                    entryKey = "CenteredOnFoe";
                    if (abilityEntries.containsKey(entryKey))
                        ((Teleport) ability).setCenteredOnFoe((Boolean) abilityEntries.get(entryKey));

                    break;
                case SNARE:
                    ability = new Snare();

                    entryKey = "Duration";
                    if (abilityEntries.containsKey(entryKey))
                        ((Snare) ability).setDuration((Integer) abilityEntries.get(entryKey));

                    entryKey = "Destructible";
                    if (abilityEntries.containsKey(entryKey))
                        ((Snare) ability).setDestructible((Boolean) abilityEntries.get(entryKey));

                    entryKey = "Radius";
                    if (abilityEntries.containsKey(entryKey))
                        ((Snare) ability).setRadius((Integer) abilityEntries.get(entryKey));

                    break;
                case COMMAND:
                    ability = new CommandAbility();

                    entryKey = "Command";
                    if (abilityEntries.containsKey(entryKey))
                        ((CommandAbility) ability).setCommand((String) abilityEntries.get(entryKey));

                    break;
                case SUMMON:
                    ability = new Summon();

                    entryKey = "Lightning";
                    if (abilityEntries.containsKey(entryKey))
                        ((Summon) ability).setLightning((Boolean) abilityEntries.get(entryKey));

                    entryKey = "MinimumAmount";
                    if (abilityEntries.containsKey(entryKey))
                        ((Summon) ability).setMinAmount((Integer) abilityEntries.get(entryKey));

                    entryKey = "MaximumAmount";
                    if (abilityEntries.containsKey(entryKey))
                        ((Summon) ability).setMaxAmount((Integer) abilityEntries.get(entryKey));

                    entryKey = "MinimumDistance";
                    if (abilityEntries.containsKey(entryKey))
                        ((Summon) ability).setMinDistance((Integer) abilityEntries.get(entryKey));

                    entryKey = "MaximumDistance";
                    if (abilityEntries.containsKey(entryKey))
                        ((Summon) ability).setMaxDistance((Integer) abilityEntries.get(entryKey));

                    entryKey = "FireResistant";
                    if (abilityEntries.containsKey(entryKey))
                        ((Summon) ability).setFireResistant((Boolean) abilityEntries.get(entryKey));

                    entryKey = "MonsterTypes";
                    if (abilityEntries.containsKey(entryKey)) {
                        ConfigurationSection monsterTypeSection = (ConfigurationSection) abilityEntries.get(entryKey);
                        Map<Summon.Type, Integer> bosses = new HashMap<Summon.Type, Integer>();
                        for (String monsterNode : monsterTypeSection.getKeys(false)) {
                            EntityType type = EntityType.fromName(monsterNode);
                            if (type == null) {
                                CorLogger.w("'" + monsterNode + "' in '" + abilityName + "' in abilities.yml is not a valid EntityType");
                                continue;
                            }
                            int chance = 1;
                            if (monsterTypeSection.isInt(monsterNode + ".Chance")) {
                                chance = monsterTypeSection.getInt(monsterNode + ".Chance");
                            }

                            double bossChance = 1;
                            if (monsterTypeSection.isDouble(monsterNode + ".BossChance")) {
                                bossChance = monsterTypeSection.getDouble(monsterNode + ".BossChance");
                            }

                            List<String> allowedBosses = new ArrayList<String>();
                            if (monsterTypeSection.isList(monsterNode + ".BossTypes")) {
                                allowedBosses = (List<String>) monsterTypeSection.getList(monsterNode + ".BossTypes");
                            }

                            Summon.Type summonType = ((Summon) ability).new Type(type, bossChance, allowedBosses);
                            bosses.put(summonType, chance);
                        }

                        ((Summon) ability).setAllowedBosses(bosses);
                    }

                    break;
                default:
                    break;
            }

            if (ability != null) {
                ability.setAbilityType(abilityType);

                ability.setName(abilityName);

                entryKey = "Message";
                if (abilityEntries.containsKey(entryKey))
                    ability.setMessage((String) abilityEntries.get(entryKey));

                entryKey = "Cooldown";
                if (abilityEntries.containsKey(entryKey))
                    ability.setCooldown((Double) abilityEntries.get(entryKey));

                entryKey = "AssignationChance";
                if (abilityEntries.containsKey(entryKey))
                    ability.setAssignationChance((Double) abilityEntries.get(entryKey));

                entryKey = "ActivationChance";
                if (abilityEntries.containsKey(entryKey))
                    ability.setActivationChance((Double) abilityEntries.get(entryKey));

                entryKey = "MinimumRange";
                if (abilityEntries.containsKey(entryKey))
                    ability.setMinRange((Integer) abilityEntries.get(entryKey));

                entryKey = "MaximumRange";
                if (abilityEntries.containsKey(entryKey))
                    ability.setMaxRange((Integer) abilityEntries.get(entryKey));

                entryKey = "ActivationConditions";
                if (abilityEntries.containsKey(entryKey)) {
                    List<String> activationConditions = yamlConfig.getConfigurationSection(abilityName).getStringList(entryKey);
                    for (String activationCondition : activationConditions) {
                        ActivationCondition condition = ActivationCondition.fromString(activationCondition);
                        if (condition != null) {
                            if (condition == ActivationCondition.ONDEATH && !ability.getAbilityType().isOnDeathAllowed()) {
                                CorLogger.i("'" + abilityName + "." + entryKey + "." + activationCondition + "' isn't possible/allowed for this ability.");
                                continue;
                            }
                            ability.addActivationCondition(condition);
                        } else {
                            CorLogger.warning("'" + abilityName + "." + entryKey + "." + activationCondition + "' isn't a valid activation condition.");
                        }
                    }
                }

                abilities.put(abilityName, ability);
            }
        }
    }

    public static Map<String, Ability> getAbilities() {
        return abilities;
    }
}
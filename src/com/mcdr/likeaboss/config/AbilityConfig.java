package com.mcdr.likeaboss.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import com.mcdr.likeaboss.Likeaboss;
import com.mcdr.likeaboss.ability.Ability;
import com.mcdr.likeaboss.ability.ArmorPierce;
import com.mcdr.likeaboss.ability.Bomb;
import com.mcdr.likeaboss.ability.FirePunch;
import com.mcdr.likeaboss.ability.Knockback;
import com.mcdr.likeaboss.ability.LightningAura;
import com.mcdr.likeaboss.ability.Potion;
import com.mcdr.likeaboss.ability.Ability.AbilityType;
import com.mcdr.likeaboss.ability.Snare;
import com.mcdr.likeaboss.ability.Teleport;

public abstract class AbilityConfig extends BaseConfig {
	private static Map<String, Ability> abilities = new HashMap<String, Ability>();
	public static void Load() {
		File file = new File(DATAFOLDER, "abilities.yml");
		
		if (!file.exists())
			copyResource(file, "com/mcdr/likeaboss/config/abilities.yml");
		
		LoadAbilities(loadConfig(file));
		
	}
	
	private static void LoadAbilities(YamlConfiguration yamlConfig) {
		Set<String> abilityNames = yamlConfig.getKeys(false);
		abilityNames.remove("ConfigVersion");
		for (String abilityName : abilityNames) {
			String node = abilityName;
			Map<String, Object> abilityEntries = yamlConfig.getConfigurationSection(node).getValues(false);
			String entryKey = "Type";
			node += "." + entryKey;
			
			if (!abilityEntries.containsKey(entryKey)) {
				Likeaboss.l.warning("[Likeaboss] '" + node + "' in abilities config file is missing.");
				continue;
			}
			
			String entryValue = yamlConfig.getString(node);
			AbilityType abilityType = AbilityType.FromString(entryValue);
			
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
				((Potion) ability).setTarget((abilityEntries.containsKey(entryKey))?(String)abilityEntries.get(entryKey):"other");
				
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
					((Bomb) ability).setFuseTicks((Integer)abilityEntries.get(entryKey));
				
				entryKey = "DestroyWorld";
				if(abilityEntries.containsKey(entryKey))
					((Bomb) ability).setDestroyWorld((Boolean) abilityEntries.get(entryKey));
				
				entryKey = "Fire";
				if(abilityEntries.containsKey(entryKey))
					((Bomb) ability).setFire((Boolean) abilityEntries.get(entryKey));
				
				entryKey = "ExplosionRadius";
				if (abilityEntries.containsKey(entryKey))
					((Bomb) ability).setRadius((float)((Integer)abilityEntries.get(entryKey)));				
				break;
				
			case LIGHTNINGAURA:
				ability = new LightningAura();
				
				entryKey = "Damage";
				if(abilityEntries.containsKey(entryKey))
					((LightningAura) ability).setDamage((Integer) abilityEntries.get(entryKey));
				
				entryKey = "Fire";
				if(abilityEntries.containsKey(entryKey))
					((LightningAura) ability).setFire((Boolean) abilityEntries.get(entryKey));
				
				entryKey = "ArmorPierce";
				if(abilityEntries.containsKey(entryKey)){
					((LightningAura) ability).setArmorPierce((Boolean) abilityEntries.get(entryKey));
				}
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
				if(abilityEntries.containsKey(entryKey))
					((Snare) ability).setDuration((Integer) abilityEntries.get(entryKey));
				
				entryKey = "Destructible";
				if(abilityEntries.containsKey(entryKey))
					((Snare) ability).setDestructible((Boolean) abilityEntries.get(entryKey));
				
				entryKey = "Radius";
				if(abilityEntries.containsKey(entryKey))
					((Snare) ability).setRadius((Integer) abilityEntries.get(entryKey));
				
				//Register events, since this ability uses an blockbreakeventlistener
				Likeaboss.in.getServer().getPluginManager().registerEvents((Listener) ability, Likeaboss.in);
				break;
			default:
				break;
			}
			
			if(ability != null){
				entryKey = "Message";
				if(abilityEntries.containsKey(entryKey))
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
				if(abilityEntries.containsKey(entryKey))
					ability.setMinRange((Integer) abilityEntries.get(entryKey));
				
				entryKey = "MaximumRange";
				if(abilityEntries.containsKey(entryKey))
					ability.setMaxRange((Integer) abilityEntries.get(entryKey));

				abilities.put(abilityName, ability);
			}
		}
	}
	
	public static Map<String, Ability> getAbilities() {
		return abilities;
	}
}
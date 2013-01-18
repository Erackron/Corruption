package com.mcdr.likeaboss.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

import com.mcdr.likeaboss.Likeaboss;
import com.mcdr.likeaboss.ability.Ability;
import com.mcdr.likeaboss.ability.ArmorPierce;
import com.mcdr.likeaboss.ability.Bomb;
import com.mcdr.likeaboss.ability.FirePunch;
import com.mcdr.likeaboss.ability.Knockback;
import com.mcdr.likeaboss.ability.LightningAura;
import com.mcdr.likeaboss.ability.Potion;
import com.mcdr.likeaboss.ability.Ability.AbilityType;

public abstract class AbilityConfig extends BaseConfig {
	private static Map<String, Ability> abilities = new HashMap<String, Ability>();
	public static void Load() {
		File file = LoadFile(Likeaboss.in.getDataFolder().getPath() + File.separator + "abilities.yml", "com" + File.separator + "mcdr" + File.separator + "likeaboss" + File.separator + "config" + File.separator + "abilities.yml");
		
		if (file == null)
			return;
		
		YamlConfiguration yamlConfig = LoadConfig(file);
		
		LoadAbilities(yamlConfig);
		
	}
	
	private static void LoadAbilities(YamlConfiguration yamlConfig) {
		Set<String> abilityNames = yamlConfig.getKeys(false);
		abilityNames.remove("version");
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
			
			switch (abilityType) {
			case ARMORPIERCE:
				ArmorPierce armorPierce = new ArmorPierce();
				
				entryKey = "Message";
				if (abilityEntries.containsKey(entryKey))
					armorPierce.setMessage((String) abilityEntries.get(entryKey));
				
				entryKey = "Cooldown";
				if (abilityEntries.containsKey(entryKey))
					armorPierce.setCooldown((Double) abilityEntries.get(entryKey));
				
				entryKey = "Value";
				if (abilityEntries.containsKey(entryKey))
					armorPierce.setValue((Double) abilityEntries.get(entryKey));
				
				entryKey = "Probability";
				if (abilityEntries.containsKey(entryKey))
					armorPierce.setChance((Double) abilityEntries.get(entryKey));
					
				abilities.put(abilityName, armorPierce);
				break;
				
			case FIREPUNCH:
				FirePunch firePunch = new FirePunch();
				
				entryKey = "Message";
				if (abilityEntries.containsKey(entryKey))
					firePunch.setMessage((String) abilityEntries.get(entryKey));
				
				entryKey = "Cooldown";
				if (abilityEntries.containsKey(entryKey))
					firePunch.setCooldown((Double) abilityEntries.get(entryKey));
					
				entryKey = "Ticks";
				if (abilityEntries.containsKey(entryKey))
					firePunch.setTicks((Integer) abilityEntries.get(entryKey));
				
				entryKey = "Probability";
				if (abilityEntries.containsKey(entryKey))
					firePunch.setChance((Double) abilityEntries.get(entryKey));
					
				abilities.put(abilityName, firePunch);
				break;
				
			case KNOCKBACK:
				Knockback knockback = new Knockback();
				
				entryKey = "Message";
				if (abilityEntries.containsKey(entryKey))
					knockback.setMessage((String) abilityEntries.get(entryKey));
				
				entryKey = "Cooldown";
				if (abilityEntries.containsKey(entryKey))
					knockback.setCooldown((Double) abilityEntries.get(entryKey));
				
				entryKey = "VerticalCoef";
				if (abilityEntries.containsKey(entryKey))
					knockback.setVerticalCoef((Double) abilityEntries.get(entryKey));
				
				entryKey = "HorizontalCoef";
				if (abilityEntries.containsKey(entryKey))
					knockback.setHorizontalCoef((Double) abilityEntries.get(entryKey));
				
				entryKey = "Probability";
				if (abilityEntries.containsKey(entryKey))
					knockback.setChance((Double) abilityEntries.get(entryKey));
				
				abilities.put(abilityName, knockback);
				break;
				
			case POTION:
				Potion potion = new Potion();
				
				entryKey = "Target";		
				potion.setTarget((abilityEntries.containsKey(entryKey))?(String)abilityEntries.get(entryKey):"other");
				
				entryKey = "Message";
				if (abilityEntries.containsKey(entryKey))
					potion.setMessage((String) abilityEntries.get(entryKey));
				
				entryKey = "Cooldown";
				if (abilityEntries.containsKey(entryKey))
					potion.setCooldown((Double) abilityEntries.get(entryKey));
					
				entryKey = "Amplifier";
				if (abilityEntries.containsKey(entryKey))
					potion.setAmplifier((Integer) abilityEntries.get(entryKey));
				
				entryKey = "Duration";
				if (abilityEntries.containsKey(entryKey))
					potion.setDuration(((Double) abilityEntries.get(entryKey)).intValue() * 20);
				
				entryKey = "Probability";
				if (abilityEntries.containsKey(entryKey))
					potion.setChance((Double) abilityEntries.get(entryKey));
				
				entryKey = "Effect";
				if (abilityEntries.containsKey(entryKey))
					potion.setEffect((String) abilityEntries.get(entryKey));
				
				abilities.put(abilityName, potion);
				break;
			
			case BOMB:
				Bomb bomb = new Bomb();
				
				entryKey = "Message";
				if (abilityEntries.containsKey(entryKey))
					bomb.setMessage((String) abilityEntries.get(entryKey));
				
				entryKey = "Cooldown";
				if (abilityEntries.containsKey(entryKey))
					bomb.setCooldown((Double) abilityEntries.get(entryKey));
				
				entryKey = "Fuse";
				if (abilityEntries.containsKey(entryKey))
					bomb.setFuseTicks((Integer)abilityEntries.get(entryKey));
				
				entryKey = "DestroyWorld";
				if(abilityEntries.containsKey(entryKey))
					bomb.setDestroyWorld((Boolean) abilityEntries.get(entryKey));
				
				entryKey = "Fire";
				if(abilityEntries.containsKey(entryKey))
					bomb.setFire((Boolean) abilityEntries.get(entryKey));
				
				entryKey = "Radius";
				if (abilityEntries.containsKey(entryKey))
					bomb.setRadius((float)((Integer)abilityEntries.get(entryKey)));
				
				entryKey = "Probability";
				if (abilityEntries.containsKey(entryKey))
					bomb.setChance((Double) abilityEntries.get(entryKey));
				
				abilities.put(abilityName, bomb);
				break;
				
			case LIGHTNINGAURA:
				LightningAura aura = new LightningAura();
				
				entryKey = "Message";
				if (abilityEntries.containsKey(entryKey))
					aura.setMessage((String) abilityEntries.get(entryKey));
				
				entryKey = "Cooldown";
				if (abilityEntries.containsKey(entryKey))
					aura.setCooldown((Double) abilityEntries.get(entryKey));
				
				entryKey = "Radius";
				if (abilityEntries.containsKey(entryKey))
					aura.setRadius((Integer) abilityEntries.get(entryKey));
				
				entryKey = "Damage";
				if(abilityEntries.containsKey(entryKey))
					aura.setDamage((Integer) abilityEntries.get(entryKey));
				
				entryKey = "Probability";
				if (abilityEntries.containsKey(entryKey))
					aura.setChance((Double) abilityEntries.get(entryKey));
				
				entryKey = "Fire";
				if(abilityEntries.containsKey(entryKey))
					aura.setFire((Boolean) abilityEntries.get(entryKey));
				
				entryKey = "ArmorPierce";
				if(abilityEntries.containsKey(entryKey)){
					aura.setArmorPierce((Boolean) abilityEntries.get(entryKey));
				}
				
				abilities.put(abilityName, aura);
				break;
				
			case UNKNOWN:
				Likeaboss.l.warning("[Likeaboss] '" + entryValue + "' in abilities config file isn't a valid ability.");
				continue;			
			default:
				break;
			}
		}
	}
	
	public static Map<String, Ability> getAbilities() {
		return abilities;
	}
}
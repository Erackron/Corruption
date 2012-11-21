package cam.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import cam.Likeaboss;
import cam.ability.Ability;
import cam.ability.Ability.AbilityType;
import cam.ability.ArmorPierce;
import cam.ability.FirePunch;
import cam.ability.Knockback;
import cam.ability.Bomb;
import cam.ability.Potion;

public abstract class AbilityConfig extends BaseConfig {
	private static Map<String, Ability> abilities = new HashMap<String, Ability>();
	public static void Load() {
		File file = LoadFile("plugins/Likeaboss/abilities.yml", "cam/config/abilities.yml");
		
		if (file == null)
			return;
		
		YamlConfiguration yamlConfig = LoadConfig(file);
		
		LoadAbilities(yamlConfig);
		
	}
	
	private static void LoadAbilities(YamlConfiguration yamlConfig) {
		Set<String> abilityNames = yamlConfig.getKeys(false);
		
		for (String abilityName : abilityNames) {
			String node = abilityName;
			Map<String, Object> abilityEntries = yamlConfig.getConfigurationSection(node).getValues(false);
			String entryKey = "Type";
			node += "." + entryKey;
			
			if (!abilityEntries.containsKey(entryKey)) {
				Likeaboss.logger.warning("[Likeaboss] '" + node + "' in abilities config file is missing.");
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
				if (abilityEntries.containsKey(entryKey)){
					bomb.setFuseTicks((int) abilityEntries.get(entryKey));
				}
				
				entryKey = "Radius";
				if (abilityEntries.containsKey(entryKey))
					bomb.setRadius((float)((int)abilityEntries.get(entryKey)));
				
				entryKey = "Probability";
				if (abilityEntries.containsKey(entryKey))
					bomb.setChance((Double) abilityEntries.get(entryKey));
				
				abilities.put(abilityName, bomb);
				break;
				
			case UNKNOWN:
				Likeaboss.logger.warning("[Likeaboss] '" + entryValue + "' in abilities config file isn't a valid ability.");
				continue;
			}
		}
	}
	
	public static Map<String, Ability> getAbilities() {
		return abilities;
	}
}

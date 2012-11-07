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
import cam.ability.Minions;
import cam.ability.ObsidianBomb;
import cam.ability.Slow;

public abstract class AbilityConfig extends BaseConfig {
	private static Map<String, Ability> abilities = new HashMap<String, Ability>();
	private Likeaboss plugin;
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
					
				entryKey = "Value";
				if (abilityEntries.containsKey(entryKey))
					armorPierce.setValue((Double) abilityEntries.get(entryKey));
					
				abilities.put(abilityName, armorPierce);
				break;
				
			case FIREPUNCH:
				FirePunch firePunch = new FirePunch();
					
				entryKey = "Ticks";
				if (abilityEntries.containsKey(entryKey))
					firePunch.setTicks((Integer) abilityEntries.get(entryKey));
					
				abilities.put(abilityName, firePunch);
				break;
				
			case KNOCKBACK:
				Knockback knockback = new Knockback();
				
				entryKey = "VerticalCoef";
				if (abilityEntries.containsKey(entryKey))
					knockback.setVerticalCoef((Double) abilityEntries.get(entryKey));
				
				entryKey = "HorizontalCoef";
				if (abilityEntries.containsKey(entryKey))
					knockback.setHorizontalCoef((Double) abilityEntries.get(entryKey));
				
				abilities.put(abilityName, knockback);
				break;
				
			case MINIONS:
				Minions minions = new Minions();
				
				entryKey = "Cooldown";
				if (abilityEntries.containsKey(entryKey))
					minions.setCooldown((Double) abilityEntries.get(entryKey));
				
				entryKey = "EntityType";
				if (abilityEntries.containsKey(entryKey))
					minions.setEntityType(EntityType.fromName(abilityEntries.get(entryKey).toString()));
				
				entryKey = "Amount";
				if (abilityEntries.containsKey(entryKey))
					minions.setAmount((Integer) abilityEntries.get(entryKey));
				
				entryKey = "Range";
				if (abilityEntries.containsKey(entryKey))
					minions.setAreaRadius((Integer) abilityEntries.get(entryKey));
				
				abilities.put(abilityName, minions);
				break;
				
			case SLOW:
				Slow snare = new Slow();
					
				entryKey = "Amplifier";
				if (abilityEntries.containsKey(entryKey))
					snare.setAmplifier((Integer) abilityEntries.get(entryKey));
				
				entryKey = "Duration";
				if (abilityEntries.containsKey(entryKey))
					snare.setDuration(((Double) abilityEntries.get(entryKey)).intValue() * 20);
					
				abilities.put(abilityName, snare);
				break;
			
			case OBSIDIANBOMB:
				ObsidianBomb ob = new ObsidianBomb();
				abilities.put(abilityName, ob);
				
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

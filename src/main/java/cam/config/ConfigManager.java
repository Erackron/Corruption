package cam.config;

import org.bukkit.World;

import cam.Likeaboss;

public abstract class ConfigManager {
	public static void Load() {
		GlobalConfig.Load();
		AbilityConfig.Load();
		BossConfig.Load();
		
		if(Likeaboss.msInstalled){
			MagicSpellsConfig.Load();
		}
		
		for (World world : Likeaboss.instance.getServer().getWorlds()) {
			WorldConfig.Load(world);
		}
	}
}

package cam.config;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.EntityType;

import cam.boss.BossData;
import cam.drop.Roll;

public class WorldConfig {
	
	private Set<Roll> rolls = new HashSet<Roll>();
	private Set<BossData> bossDatas = new HashSet<BossData>();
	
	public void AddRoll(Roll roll) {
		rolls.add(roll);
	}
	
	public void AddBossData(BossData bossData) {
		bossDatas.add(bossData);
	}
	
	public Set<Roll> getRolls() {
		return rolls;
	}
	
	public BossData getBossData(EntityType entityType) {
		for (BossData bossData : bossDatas) {
			if (bossData.getEntityType() == entityType)
				return bossData;
		}
		
		return null;
	}
}

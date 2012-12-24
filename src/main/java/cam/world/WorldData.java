package cam.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;

import cam.ability.Ability;
import cam.drop.Roll;
import cam.entity.BossData;

public class WorldData {
	private List<Roll> rolls = new ArrayList<Roll>();
	private List<BossData> bossDatas = new ArrayList<BossData>();
	private List<Ability> abilities = new ArrayList<Ability>();
	
	public void AddBossData(BossData bossData) {
		bossDatas.add(bossData);
	}
	
	public void AddRoll(Roll roll) {
		rolls.add(roll);
	}
	
	public void AddAbility(Ability ability) {
		abilities.add(ability);
	}
	
	public BossData getBossData(EntityType entityType) {
		for (BossData bossData : bossDatas) {
			if (bossData.getEntityType() == entityType)
				return bossData;
		}
		
		return null;
	}
	
	public List<Roll> getRolls() {
		return rolls;
	}
	
	public List<Ability> getAbilities() {
		return abilities;
	}
}

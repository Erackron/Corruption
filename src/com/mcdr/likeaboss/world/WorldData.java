package com.mcdr.likeaboss.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;

import com.mcdr.likeaboss.ability.Ability;
import com.mcdr.likeaboss.drop.Roll;
import com.mcdr.likeaboss.entity.BossData;


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
	
	public ArrayList<BossData> getBossData(EntityType entityType) {
		ArrayList<BossData> bosses = new ArrayList<BossData>();
		for (BossData bossData : bossDatas) {
			if (bossData.getEntityType() == entityType)
				bosses.add(bossData);
		}
		return bosses.size()>0?bosses:null;
	}
	
	public List<Roll> getRolls() {
		return rolls;
	}
	
	public List<Ability> getAbilities() {
		return abilities;
	}
}

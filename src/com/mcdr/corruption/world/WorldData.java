package com.mcdr.corruption.world;

import com.mcdr.corruption.ability.Ability;
import com.mcdr.corruption.drop.Roll;
import com.mcdr.corruption.entity.data.BossData;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;


public class WorldData {
    private List<Roll> rolls = new ArrayList<Roll>();
    private List<BossData> bossDatas = new ArrayList<BossData>();
    private List<Ability> abilities = new ArrayList<Ability>();

    public void addBossData(BossData bossData) {
        bossDatas.add(bossData);
    }

    public void addRoll(Roll roll) {
        rolls.add(roll);
    }

    public void addAbility(Ability ability) {
        abilities.add(ability);
    }

    public ArrayList<BossData> getBossData(EntityType entityType) {
        ArrayList<BossData> bosses = new ArrayList<BossData>();
        for (BossData bossData : bossDatas) {
            if (bossData.getEntityType() == entityType)
                bosses.add(bossData);
        }
        return bosses.size() > 0 ? bosses : null;
    }

    public List<Roll> getRolls() {
        return rolls;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }
}

package com.mcdr.likeaboss.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import com.mcdr.likeaboss.ability.Ability;
import com.mcdr.likeaboss.ability.Ability.ActivationCondition;
import com.mcdr.likeaboss.config.WorldConfig;
import com.mcdr.likeaboss.config.GlobalConfig.BossParam;
import com.mcdr.likeaboss.drop.DropCalculator;
import com.mcdr.likeaboss.player.LabPlayer;
import com.mcdr.likeaboss.stats.StatsManager;


public class Boss extends LabEntity {
	private BossData bossData;
	private int health;
	private Map<Ability, Boolean> abilities = new HashMap<Ability, Boolean>();
	private int fireEnchantTick;
	private LabPlayer killer;
	private boolean found;
	private int lastTimeNotified;
	
	public Boss(LivingEntity livingEntity, BossData bossData) {
		this.livingEntity = livingEntity;
		this.bossData = bossData;
		if(bossData.useHealthMultiplier())
			health = (int) (livingEntity.getMaxHealth() * bossData.getHealthCoef());
		else
			health = (int) bossData.getHealthCoef();
		
		livingEntity.setMaxHealth(health);
		livingEntity.setHealth(health);
		AddAbilities();
		if(!bossData.hasEquipment())
			bossData.setEquipment(new EquipmentSet());
		bossData.setRandomEquipment(livingEntity);
		
	}
	
	private void AddAbilities() {
		for (Ability ability : WorldConfig.getWorldData(livingEntity.getWorld()).getAbilities()) {
			//if (Utility.random.nextInt(100) < ability.getChance())
				abilities.put(ability, true);
		}
		
		for (Ability ability : bossData.getAbilities()) {
			//if (Utility.random.nextInt(100) < ability.getChance())
				abilities.put(ability, true);
		}
	}
	
	public void ActivateAbilities(EntityDamageEvent event, LivingEntity livingEntity, ActivationCondition activationCondition) {
		for (Entry<Ability, Boolean> entry : abilities.entrySet()) {
			if (entry.getValue() == false)
				continue;
			
			Ability ability = entry.getKey();
			
			if (ability.getActivationConditions().contains(activationCondition))
				ability.Execute(event, livingEntity, this);
		}
	}
	
	public BossData getBossData() {
		return bossData;
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getFireEnchantTick() {
		return fireEnchantTick;
	}
	
	public LabPlayer getKiller() {
		return killer;
	}
	
	public boolean getFound() {
		return found;
	}
	
	public int getLastTimeNotified() {
		return lastTimeNotified;
	}
	
	public void setBossData(BossData bossData) {
		this.bossData = bossData;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public void setFireEnchantTick(int fireEnchantTick) {
		this.fireEnchantTick = fireEnchantTick;
	}
	
	public void setKiller(LabPlayer killer) {
		this.killer = killer;
	}
	
	public void setFound(boolean found) {
		this.found = found;
	}
	
	public void setLastTimeNotified(int lastTimeNotified) {
		this.lastTimeNotified = lastTimeNotified;
	}
	
	@Override
	public void OnDeath(EntityDeathEvent event) {
		//Prepare drops and exp
		List<ItemStack> drops = DropCalculator.CreateDrops(getBossData(), WorldConfig.getWorldData(livingEntity.getWorld()));
		int exp;
		if(getBossData().useExperienceMultiplier())
			exp = (int) (event.getDroppedExp() * getBossData().getExpCoef());
		else
			exp = (int) getBossData().getExpCoef();
		
		//Update drops and exp
		List<ItemStack> originalDrops = event.getDrops();
		if (BossParam.OVERWRITE_DROPS.getBoolValue()){
			if(getBossData().hasEquipment()){
				EntityEquipment equips = event.getEntity().getEquipment();
				ArrayList<ItemStack> items = new ArrayList<ItemStack>();
				items.add(equips.getHelmet());
				items.add(equips.getChestplate());
				items.add(equips.getLeggings());
				items.add(equips.getBoots());
				items.add(equips.getItemInHand());
				originalDrops.retainAll(items);
			} else 
				originalDrops.clear();
			
		}
		
		originalDrops.addAll(drops);
		event.setDroppedExp(exp);
		
		LabEntityManager.RemoveBoss(this);
		
		if (killer != null) {
			killer.AddBossKilled(bossData.getName(), 1);
			StatsManager.AddBossKilled(1);
		}
	}
	
	public void ChangeAbilityStatus(Ability ability, boolean status) {
		for (Entry<Ability, Boolean> entry : abilities.entrySet()) {
			if (entry.getKey() != ability)
				continue;
			
			entry.setValue(status);
			return;
		}
	}
}

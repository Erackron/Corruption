package cam.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import cam.ability.Ability;
import cam.ability.Ability.ActivationCondition;
import cam.config.WorldConfig;
import cam.config.GlobalConfig.BossParam;
import cam.drop.DropCalculator;
import cam.player.LabPlayer;
import cam.stats.StatsManager;

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
		health = (int) (livingEntity.getMaxHealth() * bossData.getHealthCoef());
		
		AddAbilities();
		addArmor(this.bossData.getArmor(), this.bossData.getWeapon());
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
	
	private void addArmor(ItemStack[] armor, ItemStack weapon){
		switch(livingEntity.getType()){
		case PIG_ZOMBIE:
			livingEntity.getEquipment().setArmorContents(armor);
			livingEntity.getEquipment().setItemInHand(weapon);
			break;
		case SKELETON:
			livingEntity.getEquipment().setArmorContents(armor);			
			livingEntity.getEquipment().setItemInHand(weapon);
			break;
		case ZOMBIE:
			livingEntity.getEquipment().setArmorContents(armor);
			livingEntity.getEquipment().setItemInHand(weapon);
			break;
		default:
			break;		
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
		int exp = (int) (event.getDroppedExp() * getBossData().getExpCoef());
		
		//Update drops and exp
		List<ItemStack> originalDrops = event.getDrops();
		if (BossParam.OVERWRITE_DROPS.getValue())
			originalDrops.clear();
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

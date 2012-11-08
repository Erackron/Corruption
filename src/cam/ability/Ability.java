package cam.ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import cam.entity.Boss;

public abstract class Ability {
	public enum ActivationCondition {
		ONATTACK,
		ONDEFENSE,
		ONPROXIMITY;
	}
	
	public enum AbilityType {
		ARMORPIERCE,
		FIREPUNCH,
		KNOCKBACK,
		MINIONS,
		SLOW,
		UNKNOWN,
		BOMB;
		
		public static AbilityType FromString(String string) {
			for (AbilityType abilityType : AbilityType.values()) {
				if (abilityType.toString().equalsIgnoreCase(string))
					return abilityType;
			}
			
			return UNKNOWN;
		}
	}
	
	protected List<ActivationCondition> activationConditions = new ArrayList<ActivationCondition>();
	protected AbilityType abilityType;
	protected double chance = 100.0;
	
	public List<ActivationCondition> getActivationConditions() {
		return activationConditions;
	}
	
	public double getChance() {
		return chance;
	}
	
	public void AddActivationCondition(ActivationCondition activationCondition) {
		activationConditions.add(activationCondition);
	}
	
	public void setChance(double chance) {
		this.chance = chance;
	}
	
	public abstract void Execute(EntityDamageEvent event, LivingEntity livingEntity, Boss boss);
	
	public class AbilityReactivator implements Runnable {
		private Boss boss;
		private Ability ability;
		
		public AbilityReactivator(Boss boss, Ability ability) {
			this.boss = boss;
			this.ability = ability;
		}
		
		@Override
		public void run() {
			boss.ChangeAbilityStatus(ability, true);
		}
	}
}

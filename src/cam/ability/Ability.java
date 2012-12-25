package cam.ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import cam.Likeaboss;
import cam.Utility;
import cam.entity.Boss;
import cam.player.LabPlayer;
import cam.player.LabPlayerManager;

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
		POTION,
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
	private int radius = 16;
	private String msg = "";
	private double cooldown = 0.0;
	
	public List<ActivationCondition> getActivationConditions() {
		return activationConditions;
	}
	
	public double getChance() {
		return chance;
	}
	
	public boolean checkChance() {
		return (Utility.random.nextInt(100) < this.getChance())? true : false;
	}
	
	public void AddActivationCondition(ActivationCondition activationCondition) {
		activationConditions.add(activationCondition);
	}
	
	public void setChance(double chance) {
		this.chance = chance;
	}
	
	public void setMessage(String msg){
		this.msg = msg;
	}
	
	public void setCooldown(double cooldown) {
		this.cooldown = cooldown;
	}
	
	public void useCooldown(Boss boss){
		if(cooldown==0.0)
			return;
		boss.ChangeAbilityStatus(this, false);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Likeaboss.in, new AbilityReactivator(boss, this), (long) (cooldown * 20));
	}
	
	public void sendMessage(Boss boss){
		if(msg=="")
			return;
		String message = parseMsg(msg, boss);
		for (LabPlayer labPlayer : LabPlayerManager.getLabPlayers()) {
			Player player = labPlayer.getPlayer();
			if (Utility.isNear(player.getLocation(), boss.getLivingEntity().getLocation(), 0, radius)) {
				player.sendMessage(message);
			}
		}
	}
	
	public void sendMessage(Boss boss, LivingEntity target){
		if(msg=="")
			return;
		if (target instanceof Player)
			((Player) target).sendMessage(parseMsg(msg, boss));
		
	}
	
	public String parseMsg(String msg, Boss boss){
		return Utility.parseMessage(msg, boss);
	}
	
	public void Execute(EntityDamageEvent event, LivingEntity livingEntity, Boss boss){}
	
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

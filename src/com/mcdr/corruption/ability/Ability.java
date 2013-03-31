package com.mcdr.corruption.ability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.player.CorPlayer;
import com.mcdr.corruption.player.CorPlayerManager;
import com.mcdr.corruption.util.Utility;


public abstract class Ability {
	public enum ActivationCondition {
		ONATTACK,
		ONDEFENSE,
		ONPROXIMITY,
		ONDEATH;
		
		private static final Map<String, ActivationCondition> NAME_MAP = new HashMap<String, ActivationCondition>();
		
		static{
			for(ActivationCondition activationCondition: values())
				NAME_MAP.put(activationCondition.toString().toLowerCase(), activationCondition);
		}
		
		public static ActivationCondition fromString(String activationCondition) {
			if(activationCondition==null)
				return null;
			return NAME_MAP.get(activationCondition.toLowerCase());
		}
	}
	
	public enum AbilityType {
		ARMORPIERCE(false),
		BOMB(true),
		COMMAND(true),
		FIREPUNCH(false),
		KNOCKBACK(false),
		LIGHTNINGAURA(true),
		POTION(false),
		SNARE(true),
		TELEPORT(false);
		
		private final boolean onDeathAllowed;
		private static final Map<String, AbilityType> NAME_MAP = new HashMap<String, AbilityType>();
		
		AbilityType(boolean onDeathAllowed){
			this.onDeathAllowed = onDeathAllowed;
		}
		
		static{
			for(AbilityType abilityType: values())
				NAME_MAP.put(abilityType.toString().toLowerCase(), abilityType);
		}
		
		public static AbilityType fromString(String type) {
			if(type==null)
				return null;
			return NAME_MAP.get(type.toLowerCase());
		}
		
		public boolean isOnDeathAllowed(){
			return onDeathAllowed;
		}
	}
	
	protected List<ActivationCondition> activationConditions = new ArrayList<ActivationCondition>();
	protected AbilityType abilityType;
	protected double assignationChance = 100.0;
	protected double activationChance = 100.0;
	private int messageRadius = 16;
	private int minRange = 0;
	private int maxRange = 16;
	private String msg = "";
	private double cooldown = 0.0;
	
	public List<ActivationCondition> getActivationConditions() {
		return activationConditions;
	}
	
	public AbilityType getAbilityType(){
		return abilityType;
	}
	
	public double getAssignationChance() {
		return assignationChance;
	}

	public double getActivationChance() {
		return activationChance;
	}
	
	public int getMinRange(){
		return minRange;
	}
	
	public int getMaxRange(){
		return maxRange;
	}
	
	public boolean checkChance() {
		return (Utility.random.nextInt(100) < this.getActivationChance());
	}
	
	public void addActivationCondition(ActivationCondition activationCondition) {
		activationConditions.add(activationCondition);
	}
	
	public void setAbilityType(AbilityType abilityType){
		this.abilityType = abilityType;
	}
	
	public void setAssignationChance(double assignationChance) {
		this.assignationChance = assignationChance;
	}

	public void setActivationChance(double activationChance) {
		this.activationChance = activationChance;
	}
	
	public void setMessage(String msg){
		this.msg = msg;
	}
	
	public void setCooldown(double cooldown) {
		this.cooldown = cooldown;
	}
	
	public void setMinRange(int range){
		this.minRange = range;
	}
	
	public void setMaxRange(int range){
		this.maxRange = range;
	}
	
	public void useCooldown(Boss boss){
		if(cooldown==0.0)
			return;
		boss.ChangeAbilityStatus(this, false);
		Corruption.scheduler.scheduleSyncDelayedTask(Corruption.in, new AbilityReactivator(boss, this), (long) (cooldown * 20));
	}
	
	public void sendAreaMessage(Boss boss){
		sendAreaMessage(boss, null);
	}
	
	public void sendAreaMessage(Boss boss, LivingEntity fixedTarget){
		sendAreaMessage(boss.getLivingEntity().getLocation(), boss.getBossData().getName(), fixedTarget);
	}
	
	public void sendAreaMessage(Location loc, String bossName, LivingEntity fixedTarget){
		if(msg=="")
			return;
		
		String message = parseMsg(msg, bossName);
		
		Player target = null;
		if(fixedTarget!=null && fixedTarget instanceof Player){
			target = (Player) fixedTarget;
			target.sendMessage(message);
		}
		for (CorPlayer corPlayer : CorPlayerManager.getCorPlayers()) {
			Player player = corPlayer.getPlayer();
			if(player.equals(target))
				continue;
			if (Utility.isNear(player.getLocation(), loc, 0, messageRadius)) {
				player.sendMessage(message);
			}
		}
	}
	
	public void sendMessage(Boss boss, LivingEntity target){
		sendMessage(boss.getBossData().getName(), target);
	}
	
	public void sendMessage(String bossName, LivingEntity target){
		if(msg=="")
			return;
		if (target instanceof Player)
			((Player) target).sendMessage(parseMsg(msg, bossName));
		
	}
	
	public String parseMsg(String msg, String bossName){
		return Utility.parseMessage(msg, bossName);
	}
	
	/**
	 * OnDeath Execute
	 */
	public void Execute(LivingEntity livingEntity, Location lastLoc, Boss boss){
		if(!checkChance())
			return;
		
		if (!(livingEntity instanceof Player))
			return;
	}
	
	/**
	 * Normal Execute
	 */
	public void Execute(LivingEntity livingEntity, Boss boss){
		if(!checkChance())
			return;
		
		if (!(livingEntity instanceof Player))
			return;
	}
	
	public abstract Ability clone();
	
	protected void copySettings(Ability ability){
		ability.setAbilityType(this.abilityType);
		ability.setActivationChance(this.activationChance);
		ability.setAssignationChance(this.assignationChance);
		ability.setCooldown(this.cooldown);
		ability.setMaxRange(this.maxRange);
		ability.setMinRange(this.minRange);
		ability.setMessage(this.msg);
	}
	
	protected static List<Block> findValidBlocks(Location location, int minRange, int maxRange) {
		List<Block> validBlocks = new ArrayList<Block>();
		World world = location.getWorld();

		for (int x = -maxRange ; x <= maxRange ; x++) {
			for (int z = -maxRange ; z <= maxRange ; z++) {
				if (x > -minRange && x < minRange && z > -minRange && z < minRange)
					continue;

				Block block = world.getBlockAt((int) location.getX() + x, (int) location.getY(), (int) location.getZ() + z);

				if (block.isEmpty() && block.getRelative(BlockFace.UP).isEmpty() && !block.getRelative(BlockFace.DOWN).isEmpty()) {
					validBlocks.add(block);
				}
				else {
					Block nextBlock = block.getRelative(BlockFace.DOWN, maxRange);

					do {
						if (nextBlock.isEmpty() && nextBlock.getRelative(BlockFace.UP).isEmpty() && !nextBlock.getRelative(BlockFace.DOWN).isEmpty()) {
							validBlocks.add(nextBlock);
							break;
						}

						nextBlock = nextBlock.getRelative(BlockFace.UP);
					} while (nextBlock.getY() - block.getY() < maxRange);
				}
			}
		}

		return validBlocks;
	}
	
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

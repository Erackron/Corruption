package com.mcdr.likeaboss.ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.mcdr.likeaboss.Likeaboss;
import com.mcdr.likeaboss.entity.Boss;
import com.mcdr.likeaboss.player.LabPlayer;
import com.mcdr.likeaboss.player.LabPlayerManager;
import com.mcdr.likeaboss.util.Utility;


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
		BOMB,
		LIGHTNINGAURA,
		TELEPORT;
		
		public static AbilityType FromString(String string) {
			for (AbilityType abilityType : AbilityType.values()) {
				if (abilityType.toString().equalsIgnoreCase(string))
					return abilityType;
			}
			
			return null;
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
	
	public void AddActivationCondition(ActivationCondition activationCondition) {
		activationConditions.add(activationCondition);
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
		Bukkit.getScheduler().scheduleSyncDelayedTask(Likeaboss.in, new AbilityReactivator(boss, this), (long) (cooldown * 20));
	}
	
	public void sendAreaMessage(Boss boss){
		sendAreaMessage(boss, null);
	}
	
	public void sendAreaMessage(Boss boss, LivingEntity fixedTarget){
		if(msg=="")
			return;
		
		String message = parseMsg(msg, boss);
		
		Player target = null;
		if(fixedTarget!=null && fixedTarget instanceof Player){
			target = (Player) fixedTarget;
			target.sendMessage(message);
		}
		for (LabPlayer labPlayer : LabPlayerManager.getLabPlayers()) {
			Player player = labPlayer.getPlayer();
			if(player.equals(target))
				continue;
			if (Utility.isNear(player.getLocation(), boss.getLivingEntity().getLocation(), 0, messageRadius)) {
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
	
	public void Execute(LivingEntity livingEntity, Boss boss){
		if(!checkChance())
			return;
		
		if (!(livingEntity instanceof Player))
			return;
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

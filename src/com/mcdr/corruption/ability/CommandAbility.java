package com.mcdr.corruption.ability;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.util.Utility;

public class CommandAbility extends Ability {
	
	private String command;
	
	public void setCommand(String command){
		this.command = command.startsWith("/")?command.substring(1):command;
	}
	
	/**
     * OnDeath Execute
     */
	public void Execute(LivingEntity livingEntity, Location lastLoc, String bossName){
		super.Execute(livingEntity, lastLoc, bossName);
		
		dispatchCommand(bossName);
		
		sendAreaMessage(lastLoc, bossName, livingEntity);
		
	}
	
	/**
     * Normal Execute
     */
	public void Execute(LivingEntity livingEntity, Boss boss){
		super.Execute(livingEntity, boss);
		
		dispatchCommand(boss.getBossData().getName());
		
		sendAreaMessage(boss, livingEntity);
		
		useCooldown(boss);
	}
	
	private void dispatchCommand(String bossName){
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Utility.parseMessage(command, bossName));
	}
}

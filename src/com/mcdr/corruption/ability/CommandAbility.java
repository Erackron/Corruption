package com.mcdr.corruption.ability;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.util.Utility;

public class CommandAbility extends Ability {
	private String command;
	
	public CommandAbility clone(){
		CommandAbility ca = new CommandAbility();
		copySettings(ca);
		ca.setCommand(this.command);
		return ca;
	}
	
	public void setCommand(String command){
		this.command = command.startsWith("/")?command.substring(1):command;
	}
	
	/**
     * OnDeath Execute
     */
	public void Execute(LivingEntity livingEntity, Location lastLoc, Boss boss){
		super.Execute(livingEntity, lastLoc, boss);
		
		dispatchCommand(boss, livingEntity);
		
		sendAreaMessage(lastLoc, boss.getName(), livingEntity);
	}
	
	/**
     * Normal Execute
     */
	public void Execute(LivingEntity livingEntity, Boss boss){
		super.Execute(livingEntity, boss);
		
		dispatchCommand(boss, livingEntity);
		
		sendAreaMessage(boss, livingEntity);
		
		useCooldown(boss);
	}
	
	private void dispatchCommand(Boss boss, LivingEntity le){
		if(le instanceof Player)
			command = command.replace("{PLAYER}", ((Player)le).getName());
		
		Bukkit.getServer().dispatchCommand(boss, Utility.parseMessage(command, boss.getName()));
	}
}

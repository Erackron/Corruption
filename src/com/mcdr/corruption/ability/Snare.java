package com.mcdr.corruption.ability;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.entity.Boss;

public class Snare extends Ability implements Listener {
	
	private int duration;
	private boolean destructible;
	private int radius;
	
	private boolean isRunning = false;
	private List<Block> blocks = null;
	
	public int getDuration(){
		return duration;
	}
	
	public void setDuration(int duration){
		this.duration = duration;
	}
	
	public boolean isDestructible(){
		return destructible;
	}
	
	public void setDestructible(boolean destructible){
		this.destructible = destructible;
	}
	
	public int getRadius(){
		return radius;
	}
	
	public void setRadius(int radius){
		this.radius = radius;
	}
	
	public void Execute(LivingEntity livingEntity, Boss boss){
		super.Execute(livingEntity, boss);
		
		final List<Block> validBlocks = findValidBlocks(livingEntity.getLocation(),0,radius);
		if(validBlocks.isEmpty())
			return;
		
		isRunning = true;
		
		for(Block b : validBlocks)
			b.setType(Material.WEB);
		
		blocks = validBlocks;
		Corruption.scheduler.scheduleSyncDelayedTask(Corruption.in, new Runnable(){
			
			public void run() {
				for(Block b : validBlocks){
					if(b.getType()==Material.WEB)
						b.setType(Material.AIR);
				}
				isRunning = false;
				blocks = null;
			}
			
		}, duration);
		
		useCooldown(boss);
		sendAreaMessage(boss, livingEntity);		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event){
		if(isRunning){
			if(!destructible || blocks.contains(event.getBlock()))
				event.setCancelled(true);
		}
	}
}

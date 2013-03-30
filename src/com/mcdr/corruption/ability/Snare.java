package com.mcdr.corruption.ability;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.entity.Boss;

public class Snare extends Ability implements Listener {
	
	private int duration;
	private boolean destructible;
	private int radius;
	
	private boolean isRunning = false;
	private Set<List<Block>> blocks = new HashSet<List<Block>>();
	
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
	
	/**
	 * OnDeath Execute
	 */
	public void Execute(LivingEntity livingEntity, Location lastLoc, Boss boss){
		super.Execute(livingEntity, lastLoc, boss);
		
		ensnare(livingEntity);
		
		sendAreaMessage(lastLoc, boss.getName(), livingEntity);		
	}
	
	/**
	 * Normal Execute
	 */
	public void Execute(LivingEntity livingEntity, Boss boss){
		super.Execute(livingEntity, boss);
		
		ensnare(livingEntity);
		
		useCooldown(boss);
		sendAreaMessage(boss, livingEntity);		
	}
	
	private void ensnare(LivingEntity livingEntity){
		final List<Block> validBlocks = findValidBlocks(livingEntity.getLocation(),0,radius);
		if(validBlocks.isEmpty())
			return;
		
		isRunning = true;
		
		for(Block b : validBlocks)
			b.setType(Material.WEB);
		
		blocks.add(validBlocks);
		Corruption.scheduler.scheduleSyncDelayedTask(Corruption.in, new CleanUp(validBlocks, this), duration);
		
		if(!destructible){
			Bukkit.getServer().getPluginManager().registerEvents(this, Corruption.in);
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event){
		if(isRunning){
			if(!destructible || containsBlock(blocks, event.getBlock()))
				event.setCancelled(true);
		}
	}
	
	private boolean containsBlock(Set<List<Block>> list, Block b){
		for(List<Block> entry:list){
			return entry.contains(b);
		}
		return false;
	}
	
	class CleanUp implements Runnable{
		private List<Block> blocks;
		private Snare listener;
		
		public CleanUp(List<Block> blocks, Snare listener){
			this.blocks = blocks;
			this.listener = listener;
		}
		
		public void run() {
			for(Block b : blocks){
				if(b.getType()==Material.WEB)
					b.setType(Material.AIR);
			}
			isRunning = false;
			if(!destructible)
				HandlerList.unregisterAll(listener);
			blocks.clear();
		}
			
	}
}

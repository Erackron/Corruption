package com.mcdr.corruption;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.mcdr.corruption.CorMetrics.Graph;
import com.mcdr.corruption.command.CommandManager;
import com.mcdr.corruption.config.BossConfig;
import com.mcdr.corruption.config.ConfigManager;
import com.mcdr.corruption.config.GlobalConfig;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.listener.CorEntityListener;
import com.mcdr.corruption.listener.CorMagicSpellsListener;
import com.mcdr.corruption.listener.CorPlayerListener;
import com.mcdr.corruption.listener.CorWorldListener;
import com.mcdr.corruption.player.CorPlayerManager;
import com.mcdr.corruption.stats.StatsManager;
import com.mcdr.corruption.task.TaskManager;
import com.mcdr.corruption.util.CorConfigUpdater;
import com.mcdr.corruption.util.CorUpdateChecker;
import com.timvisee.manager.permissionsmanager.PermissionsManager;

public class Corruption extends JavaPlugin {
	public static Corruption in;
	public static Logger l;
	public static BukkitScheduler scheduler;
	public static boolean msInstalled, mcMMOInstalled;
	public PermissionsManager pm;
	
	public Corruption() {
		in = this;
		l = Bukkit.getLogger();
		scheduler = Bukkit.getScheduler();
	}
	
	@Override
	public void onEnable() {
		PluginManager pluginManager = getServer().getPluginManager();		
		msInstalled = pluginManager.getPlugin("MagicSpells") != null;
		mcMMOInstalled = pluginManager.getPlugin("mcMMO") != null;
		
		updateConfigs();
		ConfigManager.Load();
		CorPlayerManager.AddOnlinePlayers();
		TaskManager.start();
		
		getCommand("corruption").setExecutor(new CommandManager());
		
		setupPermissionsManager();		
		pluginManager.registerEvents(new CorEntityListener(), this);
		pluginManager.registerEvents(new CorPlayerListener(), this);
		pluginManager.registerEvents(new CorWorldListener(), this);
		
		if(msInstalled){
			l.info("["+getName()+"] MagicSpells detected!");
			pluginManager.registerEvents(new CorMagicSpellsListener(), this);	
		}
		
		if(mcMMOInstalled)
			l.info("["+getName()+"] mcMMO detected!");
		
		setupMetrics();
		
		if(GlobalConfig.checkUpdateOnStartup)
			checkUpdates();
		
		l.info("["+getName()+"] Enabled");
	}
	
	@Override
	public void onDisable() {
		CorPlayerManager.forcePlayerDataSaving();
		CorEntityManager.purgeAllBosses();
		TaskManager.stop();
		
		l.info("["+getName()+"] Disabled");
	}
	
	/**
	 * Setup the permissions manager
	 */
	public void setupPermissionsManager() {
		// Setup the permissions manager
		pm = new PermissionsManager(getServer(), this);
		pm.setup();
	}
	
	/**
	 * Get the permissions manager
	 * @return permissions manager
	 */
	public PermissionsManager getPermissionsManager() {
		return pm;
	}
	
	private void setupMetrics(){
		try {
		    CorMetrics metrics = new CorMetrics(this);
		    
		    Graph graphActive = metrics.createGraph("Active bosses");
		    for(final EntityType type : BossConfig.getEntityTypesUsed()){
		    	
			    graphActive.addPlotter(new CorMetrics.Plotter(type.getName()){		    
						
					@Override
					public int getValue() {
						return Collections.frequency(CorEntityManager.getBossEntityTypes(), type);
					}
			    });
			    
			    Graph graphKilled = metrics.createGraph("Bosses killed");
			    graphKilled.addPlotter(new CorMetrics.Plotter("Bosses killed") {
				
			    	@Override
			    	public int getValue() {
			    		return StatsManager.getBossesKilledStats();
			    	}
			    });
		    }
		    if(metrics.start())
		    	l.info("["+getName()+"] Sending metrics data");
		    else
		    	l.info("["+getName()+"] Disabled sending metrics data");
		} catch (IOException e) {
		   l.warning("["+getName()+"] Failed to contact mcstats.org");
		}
	}
	
	private void checkUpdates() {
		if(CorUpdateChecker.updateNeeded()){
			String lastVer = CorUpdateChecker.getLastVersion();
			l.info("["+getName()+"] New version available, version " + lastVer);
			getServer().broadcast(ChatColor.GOLD + "["+getName()+"] " + ChatColor.WHITE + " New version available, version " + lastVer, "cor.update");
			getServer().broadcast(ChatColor.GOLD + "["+getName()+"] " + ChatColor.WHITE + " To update, use " + ChatColor.GREEN + "/corruption update install", "cor.update");
		} else {
			l.info("["+getName()+"] No update needed, running the latest version (" + in.getDescription().getVersion() + ")");
		}
	}
	
	private void updateConfigs(){
		CorConfigUpdater updater = new CorConfigUpdater();
		updater.updateFiles();
	}
}

package com.mcdr.likeaboss;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.mcdr.likeaboss.LabMetrics.Graph;
import com.mcdr.likeaboss.command.CommandManager;
import com.mcdr.likeaboss.config.BossConfig;
import com.mcdr.likeaboss.config.ConfigManager;
import com.mcdr.likeaboss.entity.LabEntityManager;
import com.mcdr.likeaboss.listener.LabEntityListener;
import com.mcdr.likeaboss.listener.LabMagicSpellsListener;
import com.mcdr.likeaboss.listener.LabPlayerListener;
import com.mcdr.likeaboss.listener.LabWorldListener;
import com.mcdr.likeaboss.player.LabPlayerManager;
import com.mcdr.likeaboss.stats.StatsManager;
import com.mcdr.likeaboss.task.TaskManager;
import com.mcdr.likeaboss.util.LabConfigUpdater;
import com.mcdr.likeaboss.util.LabUpdateChecker;
import com.timvisee.manager.permissionsmanager.PermissionsManager;

public class Likeaboss extends JavaPlugin {
	public static Likeaboss in;
	public static Logger l;
	public static BukkitScheduler scheduler;
	public static boolean msInstalled, mcMMOInstalled;
	public PermissionsManager pm;
	
	public Likeaboss() {
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
		LabPlayerManager.AddOnlinePlayers();
		TaskManager.Start();
		
		setupPermissionsManager();		
		pluginManager.registerEvents(new LabEntityListener(), this);
		pluginManager.registerEvents(new LabPlayerListener(), this);
		pluginManager.registerEvents(new LabWorldListener(), this);
		
		if(msInstalled){
			l.info("["+getName()+"] MagicSpells detected!");
			pluginManager.registerEvents(new LabMagicSpellsListener(), this);	
		}
		
		setupMetrics();
		
		checkUpdates();
		
		l.info("["+getName()+"] Enabled");
	}
	
	@Override
	public void onDisable() {
		TaskManager.Stop();
		try {
			LabPlayerManager.SavePlayerData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
		    LabMetrics metrics = new LabMetrics(this);
		    
		    Graph graphActive = metrics.createGraph("Active bosses");
		    for(final EntityType type : BossConfig.getEntityTypesUsed()){
		    	
			    graphActive.addPlotter(new LabMetrics.Plotter(type.getName()){		    
						
					@Override
					public int getValue() {
						return Collections.frequency(LabEntityManager.getBossEntityTypes(), type);
					}
			    });
			    
			    Graph graphKilled = metrics.createGraph("Bosses killed");
			    graphKilled.addPlotter(new LabMetrics.Plotter() {
				
			    	@Override
			    	public int getValue() {
			    		return StatsManager.getBossesKilled();
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
		if(LabUpdateChecker.updateNeeded()){
			String lastVer = LabUpdateChecker.getLastVersion();
			l.info("["+getName()+"] New version available, version " + lastVer);
			getServer().broadcast("["+getName()+"] New version available, version " + lastVer +"\n To update, use /lab update install", "lab.update");
		} else {
			l.info("["+getName()+"] No update needed, running the latest version (" + in.getDescription().getVersion() + ")");
		}
	}
	
	private void updateConfigs(){
		LabConfigUpdater updater = new LabConfigUpdater();
		updater.updateFiles();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return CommandManager.Process(sender, label, args);
	}	
}

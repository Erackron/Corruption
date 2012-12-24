package cam;

import java.util.logging.Logger;

import cam.command.CommandManager;
import cam.config.ConfigManager;
import cam.listener.LabEntityListener;
import cam.listener.LabPlayerListener;
import cam.listener.LabWorldListener;
import cam.listener.MagicSpellsListener;
import cam.player.LabPlayerManager;
import cam.task.TaskManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.timvisee.manager.permissionsmanager.PermissionsManager;

public class Likeaboss extends JavaPlugin {
	public static Likeaboss instance;
	public static Logger logger;
	public static BukkitScheduler scheduler;
	public static boolean msInstalled;
	public PermissionsManager pm;
	
	public Likeaboss() {
		instance = this;
		logger = Bukkit.getLogger();
		scheduler = Bukkit.getScheduler();
	}
	
	@Override
	public void onEnable() {
		PluginManager pluginManager = getServer().getPluginManager();		
		msInstalled = pluginManager.getPlugin("MagicSpells") != null;
		
		if(msInstalled){
			logger.info("[Likeaboss] MagicSpells detected!");
		}
		
		ConfigManager.Load();
		LabPlayerManager.AddOnlinePlayers();
		TaskManager.Start();
		
		setupPermissionsManager();		
		pluginManager.registerEvents(new LabEntityListener(), this);
		pluginManager.registerEvents(new LabPlayerListener(), this);
		pluginManager.registerEvents(new LabWorldListener(), this);
		
		if(msInstalled){
			pluginManager.registerEvents(new MagicSpellsListener(), this);			
		}
		
		logger.info("[Likeaboss] Enabled.");
	}
	
	@Override
	public void onDisable() {
		TaskManager.Stop();
		try {
			LabPlayerManager.SavePlayerData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info("[Likeaboss] Disabled.");
	}
	
	/**
	 * Setup the permissions manager
	 */
	public void setupPermissionsManager() {
		// Setup the permissions manager
		this.pm = new PermissionsManager(this.getServer(), this);
		this.pm.setup();
	}
	
	/**
	 * Get the permissions manager
	 * @return permissions manager
	 */
	public PermissionsManager getPermissionsManager() {
		return this.pm;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return CommandManager.Process(sender, label, args);
	}	
}

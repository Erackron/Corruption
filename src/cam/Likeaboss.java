package cam;

// TODO - Message containing which corrupted attacked
// TODO - Witch support
// TODO - Abilities
// TODO - Add API for MagicSpells
// TODO - Fix bug that makes LAB lose track of its monsters
// TODO - Use SpiderJockey API if available

import java.util.logging.Logger;

import cam.command.CommandManager;
import cam.config.ConfigManager;
import cam.listener.LabEntityListener;
import cam.listener.LabPlayerListener;
import cam.listener.LabWorldListener;
import cam.player.LabPlayerManager;
import cam.task.TaskManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Likeaboss extends JavaPlugin {
	public static Likeaboss instance;
	public static Logger logger;
	public static BukkitScheduler scheduler;
	
	public Likeaboss() {
		instance = this;
		logger = Bukkit.getLogger();
		scheduler = Bukkit.getScheduler();
	}
	
	@Override
	public void onEnable() {
		ConfigManager.Load();
		LabPlayerManager.AddOnlinePlayers();
		TaskManager.Start();
		
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new LabEntityListener(), this);
		pluginManager.registerEvents(new LabPlayerListener(), this);
		pluginManager.registerEvents(new LabWorldListener(), this);
		
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
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return CommandManager.Process(sender, label, args);
	}	
}

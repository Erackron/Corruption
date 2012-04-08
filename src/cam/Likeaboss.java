package cam;

import java.util.logging.Logger;

import cam.boss.BossManager;
import cam.command.CommandManager;
import cam.config.LabConfig;
import cam.drop.DropCalculator;
import cam.listener.LabEntityListener;
import cam.listener.LabPlayerListener;
import cam.listener.LabWorldListener;
import cam.player.LabPlayerManager;
import cam.stats.Stats;
import cam.task.TaskManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Likeaboss extends JavaPlugin {
	
	public static Logger log = Logger.getLogger("Minecraft");
	public static Likeaboss instance;
	
	private BossManager bossManager;
	private LabPlayerManager labPlayerManager;
	private TaskManager taskManager;
	private LabConfig labConfig;
	private DropCalculator dropCalculator;
	private Stats stats;
	private LabEntityListener labEntityListener;
	private LabPlayerListener labPlayerListener;
	private LabWorldListener labWorldListener;
	private CommandManager commandManager;
	
	@Override
	public void onEnable() {
		instance = this;
		
		bossManager = new BossManager();
		labPlayerManager = new LabPlayerManager();
		taskManager = new TaskManager();
		labConfig = new LabConfig();
		dropCalculator = new DropCalculator();
		stats = new Stats();
		labEntityListener = new LabEntityListener();
		labPlayerListener = new LabPlayerListener();
		labWorldListener = new LabWorldListener();
		commandManager = new CommandManager();
		
		labConfig.LoadFiles();
		labPlayerManager.AddOnlinePlayers();
		taskManager.setBukkitScheduler(getServer().getScheduler());
		taskManager.Start();
		
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(labEntityListener, this);
		pluginManager.registerEvents(labPlayerListener, this);
		pluginManager.registerEvents(labWorldListener, this);
		
		log.info("[Likeaboss] Enabled.");
	}
	
	@Override
	public void onDisable() {
		taskManager.Stop();
		try {
			labPlayerManager.SavePlayerData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("[Likeaboss] Disabled.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return commandManager.Process(sender, label, args);
	}
	
	public BossManager getBossManager() {
		return bossManager;
	}
	
	public LabPlayerManager getLabPlayerManager() {
		return labPlayerManager;
	}
	
	public TaskManager getTaskManager() {
		return taskManager;
	}
	
	public DropCalculator getDropCalculator() {
		return dropCalculator;
	}
	
	public Stats getStats() {
		return stats;
	}
	
	public LabEntityListener getLabEntityListener() {
		return labEntityListener;
	}
	
	public LabPlayerListener getLabPlayerListener() {
		return labPlayerListener;
	}
	
	public LabConfig getLabConfig() {
		return labConfig;
	}
}

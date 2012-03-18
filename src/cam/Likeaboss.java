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
import cam.task.TaskManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Likeaboss extends JavaPlugin {
	
	public static Logger log = Logger.getLogger("Minecraft");
	
	private BossManager bossManager = new BossManager();
	private LabPlayerManager labPlayerManager = new LabPlayerManager();
	private TaskManager taskManager = new TaskManager(this);
	private LabConfig labConfig = new LabConfig(this);
	private DropCalculator dropCalculator = new DropCalculator(this);
	private LabEntityListener labEntityListener = new LabEntityListener(this);
	private LabPlayerListener labPlayerListener = new LabPlayerListener(this);
	private LabWorldListener labWorldListener = new LabWorldListener(this);
	private CommandManager commandManager = new CommandManager(this);
	
	@Override
	public void onEnable() {
		labConfig.LoadFiles();
		try {
			labPlayerManager.LoadFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		labPlayerManager.AddOnlinePlayers(this);
		taskManager.Start();
		
		PluginManager pluginManager = this.getServer().getPluginManager();
		pluginManager.registerEvents(labEntityListener, this);
		pluginManager.registerEvents(labPlayerListener, this);
		pluginManager.registerEvents(labWorldListener, this);
		
		log.info("[Likeaboss] Enabled.");
	}
	
	@Override
	public void onDisable() {
		try {
			labPlayerManager.SaveFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		taskManager.Stop();
		
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

package cam;

import java.util.logging.Logger;

import cam.boss.BossManager;
import cam.boss.BossTaskManager;
import cam.command.CommandManager;
import cam.config.LabConfig;
import cam.drop.DropCalculator;
import cam.listener.LabEntityListener;
import cam.listener.LabPlayerListener;
import cam.player.LabPlayerManager;
import cam.player.LabPlayerTaskManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Likeaboss extends JavaPlugin {
	
	public static Logger log = Logger.getLogger("Minecraft");
	
	private BossManager bossManager = new BossManager();
	private BossTaskManager bossTaskManager = new BossTaskManager(this);
	private LabPlayerManager labPlayerManager = new LabPlayerManager();
	private LabPlayerTaskManager labPlayerTaskManager = new LabPlayerTaskManager(this);
	private LabConfig labConfig = new LabConfig(this);
	private DropCalculator dropCalculator = new DropCalculator(this);
	private LabEntityListener labEntityListener = new LabEntityListener(this);
	private LabPlayerListener labPlayerListener = new LabPlayerListener(this);
	private CommandManager commandManager = new CommandManager(this);
	
	public void onEnable() {
		labConfig.LoadFiles();
		
		bossTaskManager.Start();
		try {
			labPlayerManager.LoadFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		labPlayerManager.AddOnlinePlayers(this);
		labPlayerTaskManager.Start();
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(labEntityListener, this);
		pm.registerEvents(labPlayerListener, this);
		
		log.info("[Likeaboss] Enabled.");
	}
	
	public void onDisable() {
		bossTaskManager.Stop();
		try {
			labPlayerManager.SaveFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		labPlayerTaskManager.Stop();
		
		log.info("[Likeaboss] Disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return commandManager.Process(sender, label, args);
	}
	
	public BossManager getBossManager() {
		return bossManager;
	}

	public BossTaskManager getBossTaskManager() {
		return bossTaskManager;
	}

	public LabPlayerManager getLabPlayerManager() {
		return labPlayerManager;
	}
	
	public LabPlayerTaskManager getLabPlayerTaskManager() {
		return labPlayerTaskManager;
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

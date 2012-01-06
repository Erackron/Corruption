package cam;

import java.util.logging.Logger;

import cam.boss.BossManager;
import cam.boss.BossTask;
import cam.command.CommandBase;
import cam.config.LabConfig;
import cam.listener.LabEntityListener;
import cam.listener.LabPlayerListener;
import cam.player.LabPlayerManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Likeaboss extends JavaPlugin {
	
	public static Logger log = Logger.getLogger("Minecraft");
	
	private LabConfig labConfig = new LabConfig();
	private BossManager bossManager = new BossManager();
	private BossTask bossTask = new BossTask(this, bossManager);
	private LabPlayerManager labPlayerManager = new LabPlayerManager();
	private LabPlayerListener labPlayerListener = new LabPlayerListener(labPlayerManager);
	private LabEntityListener labEntityListener = new LabEntityListener(bossManager, labPlayerManager);
	private CommandBase commandBase = new CommandBase(this);
	
	public void onEnable() {
		log.info("[Likeaboss] Enabled");
		
		labConfig.LoadFile(this);
		
		bossTask.Start(LabConfig.Entry.TASK_CHECKENTITYHEALTH.getValue(), LabConfig.Entry.TASK_CHECKENTITYEXISTENCE.getValue());
			
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_QUIT, labPlayerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.CREATURE_SPAWN, labEntityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, labEntityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, labEntityListener, Event.Priority.Normal, this);
	}
	
	public void onDisable() {
		bossTask.Stop();
		
		log.info("[Likeaboss] Disabled");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return commandBase.Process(sender, label, args);
	}
	
	public LabConfig getLabConfig() {
		return labConfig;
	}

	public BossManager getBossManager() {
		return bossManager;
	}

	public BossTask getBossTask() {
		return bossTask;
	}

	public LabPlayerManager getLabPlayerManager() {
		return labPlayerManager;
	}

	public LabPlayerListener getLabPlayerListener() {
		return labPlayerListener;
	}

	public LabEntityListener getLabEntityListener() {
		return labEntityListener;
	}
}
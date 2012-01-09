package cam;

import java.util.logging.Logger;

import cam.boss.BossManager;
import cam.boss.BossTaskManager;
import cam.boss.DropManager;
import cam.command.CommandManager;
import cam.config.LabConfig;
import cam.listener.LabEntityListener;
import cam.listener.LabPlayerListener;
import cam.player.LabPlayerManager;
import cam.player.LabPlayerTaskManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Likeaboss extends JavaPlugin {
	
	public static Logger log = Logger.getLogger("Minecraft");
	
	private DropManager dropManager = new DropManager();
	private LabConfig labConfig = new LabConfig(dropManager);
	private BossManager bossManager = new BossManager(dropManager);
	private BossTaskManager bossTaskManager = new BossTaskManager(this);
	private LabPlayerManager labPlayerManager = new LabPlayerManager();
	private LabPlayerTaskManager labPlayerTaskManager = new LabPlayerTaskManager(this);
	private LabEntityListener labEntityListener = new LabEntityListener(this);
	private LabPlayerListener labPlayerListener = new LabPlayerListener(this);
	private CommandManager commandManager = new CommandManager(this);
	
	public void onEnable() {
		log.info("[Likeaboss] Enabled.");
		
		labConfig.LoadFile(this);
		
		bossTaskManager.Start();
		labPlayerManager.AddOnlinePlayers(this);
		labPlayerTaskManager.Start();
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.CREATURE_SPAWN, labEntityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, labEntityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, labEntityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, labEntityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, labPlayerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, labPlayerListener, Event.Priority.Normal, this);
	}
	
	public void onDisable() {
		bossTaskManager.Stop();
		labPlayerTaskManager.Stop();
		
		log.info("[Likeaboss] Disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return commandManager.Process(sender, label, args);
	}
	
	public DropManager getDropManager() {
		return dropManager;
	}
	
	public LabConfig getLabConfig() {
		return labConfig;
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

	public LabEntityListener getLabEntityListener() {
		return labEntityListener;
	}
	
	public LabPlayerListener getLabPlayerListener() {
		return labPlayerListener;
	}
}
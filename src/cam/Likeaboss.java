package cam;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import cam.boss.Boss;
import cam.boss.BossManager;
import cam.boss.BossTimerTask;
import cam.listener.LabEntityListener;
import cam.listener.LabPlayerListener;
import cam.player.LabPlayer;
import cam.player.LabPlayerManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Likeaboss extends JavaPlugin {
	
	private Logger log = Logger.getLogger("Minecraft");
	private Config config = new Config();
	private BossManager bossManager = new BossManager();
	private BossTimerTask bossTimerTask = new BossTimerTask(this, bossManager);
	private LabPlayerManager labPlayerManager = new LabPlayerManager();
	private LabPlayerListener labPlayerListener = new LabPlayerListener(labPlayerManager);
	private LabEntityListener labEntityListener = new LabEntityListener(config, bossManager, labPlayerManager);
	
	public void onEnable() {
		log.info("[Likeaboss] Enabled");
		
		config.LoadFile(this);
		
		BukkitScheduler bukkitScheduler = this.getServer().getScheduler();
		bukkitScheduler.scheduleAsyncRepeatingTask(this, bossTimerTask, 0, 40);
				
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_QUIT, labPlayerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.CREATURE_SPAWN, labEntityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, labEntityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, labEntityListener, Event.Priority.Normal, this);
	}
	
	public void onDisable() {
		this.getServer().getScheduler().cancelAllTasks();
		
		log.info("[Likeaboss] Disabled");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("lab")) {
			if (args.length > 0) {
				String arg = args[0].toLowerCase();

				if (arg.equals("clear") && sender.hasPermission("lab." + arg)) {
					bossManager.clear();
					sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Cleared");
					return true;
				}
				
				else if (arg.equals("reload") && sender.hasPermission("lab." + arg)) {
					config.LoadFile(this);
					
					BukkitScheduler bukkitScheduler = this.getServer().getScheduler();
					bukkitScheduler.cancelAllTasks();
					bukkitScheduler.scheduleAsyncRepeatingTask(this, bossTimerTask, 0, 40);
					
					sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Reloaded");
					return true;
				}
				
				else if (arg.equals("info") && sender.hasPermission("lab." + arg)) {
					sender.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Info");
					sender.sendMessage(ChatColor.GRAY + "Boss Killed: " + bossManager.getBossKilled());
					sender.sendMessage(ChatColor.GRAY + "Boss Count: " + bossManager.getBossCount());
					
					Map<Material, Integer> droped = bossManager.getDroped();
					for (Entry<Material, Integer> entry : droped.entrySet())
						sender.sendMessage(ChatColor.GRAY + entry.getKey().toString() + " found: " + entry.getValue());
					return true;
				}
				
				else if (sender instanceof Player) {
					Player player = (Player) sender;

					if (arg.equals("viewer") && player.hasPermission("lab." + arg)) {
						LabPlayer labPlayer = null;
						
						if (labPlayerManager.IsLabPlayer(player)) {
							labPlayer = labPlayerManager.getLabPlayer(player);
							
							if (labPlayer.getViewer())
								labPlayer.setViewer(false);
							else
								labPlayer.setViewer(true);
						}
						
						else {
							labPlayer = new LabPlayer(player);
							labPlayer.setViewer(true);
							labPlayerManager.AddLabPlayer(labPlayer);
						}
						
						player.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Viewer: " + ChatColor.GREEN + labPlayer.getViewer());
						
						return true;
					}
					
					else if (arg.equals("list") && player.hasPermission("lab." + arg)) {
						player.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Boss List");
						
						List<Boss> bosses = bossManager.getBosses();
						Map<LivingEntity, Double> unsortedData = new HashMap<LivingEntity, Double>();
						
						for (Boss boss : bosses) {
							LivingEntity livingEntity = boss.getLivingEntity();
							
							double distance = 0;
							if (livingEntity.getWorld().equals(player.getWorld()))
								distance = livingEntity.getLocation().distance(player.getLocation());
							
							unsortedData.put(livingEntity, distance);
						}
						
						ValueComparator vc = new ValueComparator(unsortedData);
						Map<LivingEntity, Double> sortedData = new TreeMap<LivingEntity, Double>(vc);
						sortedData.putAll(unsortedData);
						
						for (Entry<LivingEntity, Double> entry : sortedData.entrySet()) {
							int distance = (int) Math.round(entry.getValue());
							LivingEntity livingEntity = entry.getKey();
							
							Location location = livingEntity.getLocation();
							String world = location.getWorld().getName();
							int x = (int) Math.round(location.getX());
							int y = (int) Math.round(location.getY());
							int z = (int) Math.round(location.getZ());
							
							String message = ChatColor.GRAY + livingEntity.getClass().getSimpleName().substring(5) + ":  ([" + world + "], " + x + ", " + y + ", " + z + ")";
							if (distance > 0)
								message += "  Dist: " + distance;
							
							player.sendMessage(message);
						}
						
						return true;
					}
				}
			}
		}

		return false;
	}
}

class ValueComparator implements Comparator<LivingEntity> {

	private Map<?, ?> base;
	
	public ValueComparator(Map<?, ?> base) {
		this.base = base;
	}
	
	public int compare(LivingEntity a, LivingEntity b) {
		if ((Double) base.get(a) > (Double) base.get(b)) 
			return 1;
		else if ((Double) base.get(a) == (Double) base.get(b))
			return 0;
		else
			return -1;
	}
}

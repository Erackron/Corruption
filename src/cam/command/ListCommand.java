package cam.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import cam.Utility.MapValueComparator;
import cam.boss.Boss;
import cam.boss.BossManager;

public abstract class ListCommand extends CommandBase {

	public static void Process() {
		if (!CheckPermission("lab.list", false))
			return;
		
		Player player = (Player) sender;
		
		BossManager bossManager = plugin.getBossManager();
		List<Boss> bosses = bossManager.getBosses();
		Map<LivingEntity, Double> unsortedData = new HashMap<LivingEntity, Double>();
		
		player.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Boss List");
		
		for (Boss boss : bosses) {
			LivingEntity livingEntity = boss.getLivingEntity();
			
			double distance = 0;
			if (livingEntity.getWorld().equals(player.getWorld()))
				distance = livingEntity.getLocation().distance(player.getLocation());
			
			unsortedData.put(livingEntity, distance);
		}
		
		MapValueComparator mapValueComparator = new MapValueComparator(unsortedData);
		Map<LivingEntity, Double> sortedData = new TreeMap<LivingEntity, Double>(mapValueComparator);
		sortedData.putAll(unsortedData);
		
		for (Entry<LivingEntity, Double> entry : sortedData.entrySet()) {
			int distance = (int) Math.round(entry.getValue());
			LivingEntity livingEntity = entry.getKey();
			
			Location location = livingEntity.getLocation();
			String world = location.getWorld().getName();
			int x = (int) Math.round(location.getX());
			int y = (int) Math.round(location.getY());
			int z = (int) Math.round(location.getZ());
			
			String name = livingEntity.getClass().getSimpleName().replace("Craft", "");
			String message = ChatColor.GRAY + name + ":  ([" + world + "], " + x + ", " + y + ", " + z + ")";
			if (distance > 0)
				message += "  Dist: " + distance;
			
			sender.sendMessage(message);
		}
	}
}

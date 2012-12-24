package cam.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import cam.Utility;
import cam.entity.Boss;
import cam.entity.LabEntityManager;

public abstract class ListCommand extends BaseCommand {
	public static void Process() {
		if (!CheckPermission("lab.list", false))
			return;
		
		Player player = (Player) sender;
		Map<Boss, Double> unsortedMap = new HashMap<Boss, Double>();
		
		player.sendMessage(ChatColor.GOLD + "[LAB] " + ChatColor.WHITE + "Boss List");
		
		for (Boss boss : LabEntityManager.getBosses()) {
			double distance = 0;
			LivingEntity livingEntity = boss.getLivingEntity();
			
			if (livingEntity.getWorld().equals(player.getWorld()))
				distance = livingEntity.getLocation().distance(player.getLocation());
			
			unsortedMap.put(boss, distance);
		}
		
		Set<Entry<Boss, Double>> sortedEntries = Utility.SortEntriesByValues(unsortedMap, true);
		
		for (Entry<Boss, Double> entry : sortedEntries) {
			Boss boss = entry.getKey();
			int distance = (int) Math.round(entry.getValue());
			LivingEntity livingEntity = boss.getLivingEntity();
			Location location = livingEntity.getLocation();
			int x = (int) Math.round(location.getX());
			int y = (int) Math.round(location.getY());
			int z = (int) Math.round(location.getZ());
			String message = ChatColor.GRAY + boss.getBossData().getName() + ":  ([" + location.getWorld().getName() + "], " + x + ", " + y + ", " + z + ")";
			
			if (distance > 0)
				message += "  Dist: " + distance;
			
			sender.sendMessage(message);
		}
	}
}

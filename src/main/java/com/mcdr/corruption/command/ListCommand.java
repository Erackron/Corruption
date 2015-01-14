package com.mcdr.corruption.command;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.util.MathUtil;
import com.mcdr.corruption.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public abstract class ListCommand extends BaseCommand {
    public static void process() {
        if (!checkPermission("cor.list", false))
            return;

        Player player = (Player) sender;
        Map<Boss, Double> unsortedMap = new HashMap<Boss, Double>();

        player.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Boss List");

        for (Boss boss : CorEntityManager.getBosses()) {
            double distance = 0;
            LivingEntity livingEntity = boss.getLivingEntity();

            if (livingEntity.getWorld().equals(player.getWorld()))
                distance = livingEntity.getLocation().distance(player.getLocation());

            unsortedMap.put(boss, distance);
        }

        Set<Entry<Boss, Double>> sortedEntries = MathUtil.sortEntriesByValues(unsortedMap, true);

        for (Entry<Boss, Double> entry : sortedEntries) {
            Boss boss = entry.getKey();
            int distance = (int) Math.round(entry.getValue());
            LivingEntity livingEntity = boss.getLivingEntity();
            Location location = livingEntity.getLocation();
            int x = (int) Math.round(location.getX());
            int y = (int) Math.round(location.getY());
            int z = (int) Math.round(location.getZ());
            String message = ChatColor.GRAY + MessageUtil.parseMessage("{BOSSNAME}", boss) + ":  ([" + location.getWorld().getName() + "], " + x + ", " + y + ", " + z + ")";

            if (distance > 0)
                message += "  Dist: " + distance;

            sender.sendMessage(message);
        }
    }
}

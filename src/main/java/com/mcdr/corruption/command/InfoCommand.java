package com.mcdr.corruption.command;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.stats.StatsManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Map;
import java.util.Map.Entry;


public abstract class InfoCommand extends BaseCommand {
    public static void process() {
        if (!checkPermission("cor.info", true))
            return;

        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Info");
        sender.sendMessage(ChatColor.GRAY + "Corrupted Killed: " + StatsManager.getBossesKilled());
        sender.sendMessage(ChatColor.GRAY + "Corrupted Count: " + CorEntityManager.getBosses().size());

        Map<Material, Integer> droped = StatsManager.getDroped();
        for (Entry<Material, Integer> entry : droped.entrySet())
            sender.sendMessage(ChatColor.GRAY + entry.getKey().toString() + " found: " + entry.getValue());
    }
}

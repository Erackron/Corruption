package com.mcdr.corruption.command;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.stats.StatsManager;
import org.bukkit.ChatColor;


public abstract class ClearCommand extends BaseCommand {
    public static void process() {
        if (!checkPermission("cor.clear", true))
            return;

        StatsManager.Clear();

        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Cleared");
    }
}

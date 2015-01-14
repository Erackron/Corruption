package com.mcdr.corruption.command;

import com.mcdr.corruption.Corruption;
import org.bukkit.ChatColor;

public class VersionCommand extends BaseCommand {
    public static void process() {
        if (!checkPermission("cor.version", true))
            return;

        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "]" + ChatColor.WHITE + " Version " + Corruption.getInstance().getDescription().getVersion());
    }
}

package com.mcdr.corruption.command;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.GlobalConfig;
import com.mcdr.corruption.logger.CorLogger;
import com.mcdr.corruption.logger.CorLogger.LogLevel;
import org.bukkit.ChatColor;

import java.util.logging.Level;

public class LogCommand extends BaseCommand {

    public static void process() {
        if (!checkPermission("cor.loglevel", true))
            return;

        int curLevel = CorLogger.getLogLevelInt();

        if (args.length < 2) {
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "The current logging level is: " + ChatColor.GRAY + CorLogger.getLogLevel().getName() + " (int: " + curLevel + ")");
        } else {
            try {
                Level newLevel;
                if (args[1].equalsIgnoreCase("reset"))
                    newLevel = LogLevel.INFO;
                else
                    newLevel = LogLevel.parse(args[1].toUpperCase());
                CorLogger.setLogLevel(newLevel);
            } catch (IllegalArgumentException ex) {
                sender.sendMessage("The supplied level is invalid");
                return;
            }

            if (curLevel != CorLogger.getLogLevelInt()) {
                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Set the logging level to: " + ChatColor.GRAY + CorLogger.getLogLevel().getName() + " (int: " + CorLogger.getLogLevel() + ")");
                GlobalConfig.save();
            } else {
                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "The logging level did not change: " + ChatColor.GRAY + CorLogger.getLogLevel().getName() + " (int: " + CorLogger.getLogLevel() + ")");
            }
        }
    }
}

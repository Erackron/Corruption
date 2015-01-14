package com.mcdr.corruption.command;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.player.CorPlayer;
import com.mcdr.corruption.player.CorPlayerData;
import com.mcdr.corruption.player.CorPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public abstract class ViewerCommand extends BaseCommand {
    public static void process() {
        if (!checkPermission("cor.viewer", false))
            return;

        CorPlayer corPlayer = CorPlayerManager.getCorPlayer((Player) sender);

        if (corPlayer == null) {
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Oops, something went wrong.");
            sender.sendMessage("Please notify the plugin author.");
        } else {
            CorPlayerData corPlayerData = corPlayer.getCorPlayerData();
            boolean viewer = corPlayerData.getViewer();

            corPlayerData.setViewer(!viewer);
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Viewer: " + ChatColor.GREEN + !viewer);
        }
    }
}

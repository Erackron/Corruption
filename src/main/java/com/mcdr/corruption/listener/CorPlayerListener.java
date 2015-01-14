package com.mcdr.corruption.listener;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.permissions.Permissions;
import com.mcdr.corruption.player.CorPlayerManager;
import com.mcdr.corruption.updater.CorUpdateChecker;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class CorPlayerListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (CorUpdateChecker.isUpdateNeeded() && Permissions.hasPermission(player, "cor.update")) {
            player.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + " New version available, version " + CorUpdateChecker.lastVer);
            player.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + " To update, use " + ChatColor.GREEN + "/lab update install");
        }
        CorPlayerManager.addCorPlayer(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        CorPlayerManager.removeCorPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        World world = event.getFrom();
        if (world.getPlayers().isEmpty()) {
            CorEntityManager.purgeBosses(world);
        }
    }
}

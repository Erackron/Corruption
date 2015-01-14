package com.mcdr.corruption.permissions;

import com.mcdr.corruption.Corruption;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class is used to provide easy access to the permission interface in a static way.
 */
public abstract class Permissions {

    /**
     * Check whether a CommandSender has the given permission.
     *
     * @param sender     The ComandSender to check the permission of
     * @param permission The permission to check
     * @return Whether the CommandSender has the specified permission
     */
    public static boolean hasPermission(CommandSender sender, String permission) {
        return !(sender instanceof Player) || sender.isOp() || hasPermission((Player) sender, permission);
    }

    /**
     * Check whether a Player has the given permission.
     *
     * @param player     The Player to check the permission of
     * @param permission The permission to check
     * @return Whether the Player has the specified permission
     */
    public static boolean hasPermission(Player player, String permission) {
        return Corruption.getInstance().pm.hasPermission(player, permission);
    }
}

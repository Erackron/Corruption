package com.mcdr.corruption.command;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.BossConfig;
import com.mcdr.corruption.config.GlobalConfig.CommandParam;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.entity.data.BossData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;

import java.util.Map;
import java.util.Map.Entry;


public abstract class SpawnCommand extends BaseCommand {
    public static void process() {
        if (!checkPermission("cor.spawn", true))
            return;

        Map<String, BossData> bossesData = BossConfig.getBossesData();

        if (args.length < 2) {
            SendUsage(bossesData);
            return;
        }

        if (sender instanceof ConsoleCommandSender && !(args.length == 4 || args.length >= 7)) {
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + "Console usage of this command requires coordinates or a username:");
            sender.sendMessage("/" + label + " spawn [type] [amount] [x] [y] [z] [World] or /" + label + " spawn [type] <amount> <player>");
            return;
        } else if (sender instanceof BlockCommandSender && !(args.length == 4 || args.length >= 6)) {
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + "Command block usage of this command requires coordinates or a username:");
            sender.sendMessage("/" + label + " spawn [type] [amount] [x] [y] [z] <World> or /" + label + " spawn [type] <amount> <player>");
            return;
        }

        for (Entry<String, BossData> bossesDataEntry : bossesData.entrySet()) {
            if (!bossesDataEntry.getKey().equalsIgnoreCase(args[1]))
                continue;

            int amount = 1;

            if (args.length >= 3) {
                try {
                    amount = Math.abs(Integer.parseInt(args[2]));
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.GRAY + args[2] + ChatColor.WHITE + " isn't an integer.");
                    return;
                }

                int max = CommandParam.SPAWN_MAX.getValue();
                if (amount > max) {
                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + "You are not allowed to spawn more than " + ChatColor.GRAY + max + ChatColor.WHITE + " boss(es) at a time.");
                    return;
                }
            }
            Location loc = getLocation();
            if (loc != null)
                Spawn(loc, bossesDataEntry.getValue(), amount);
            return;
        }

        //Invalid creature name
        SendUsage(bossesData);
    }

    @SuppressWarnings("deprecation")
    private static Location getLocation() {
        Location loc = null;
        double x, y, z;
        World w;

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length >= 6) {
                try {
                    x = Double.parseDouble(args[3]);
                    y = Double.parseDouble(args[4]);
                    z = Double.parseDouble(args[5]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + " (" + ChatColor.GRAY + args[3] + ", " + args[4] + ", " + args[5] + ChatColor.WHITE + ") aren't valid (x, y, z) coordinates.");
                    return null;
                }
                if (args.length >= 7) {
                    w = Bukkit.getServer().getWorld(args[6]);
                    if (w == null) {
                        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.GRAY + args[6] + ChatColor.WHITE + " isn't a valid World.");
                        return null;
                    }
                } else {
                    w = player.getWorld();
                }
                loc = new Location(w, x, y, z);
            } else if (args.length == 4) {
                Player otherPlayer = Bukkit.getServer().getPlayer(args[3]);
                if (otherPlayer != null) {
                    loc = otherPlayer.getTargetBlock(null, 100).getRelative(BlockFace.UP).getLocation(); //TODO Find alternative for LivingEntity.getTargetBlock(HashSet<Byte> transparent, int maxDistance)
                    loc.setWorld(otherPlayer.getWorld());
                } else {
                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + "Specified player" + ChatColor.GRAY + args[3] + ChatColor.WHITE + " isn't online.");
                    return null;
                }
            } else {
                loc = player.getTargetBlock(null, 100).getRelative(BlockFace.UP).getLocation(); //TODO Find alternative for LivingEntity.getTargetBlock(HashSet<Byte> transparent, int maxDistance)
                loc.setWorld(player.getWorld());
            }
        } else if (sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender) {
            if (args.length == 4) {
                Player player = Bukkit.getServer().getPlayer(args[3]);
                if (player != null) {
                    loc = player.getTargetBlock(null, 100).getRelative(BlockFace.UP).getLocation(); //TODO Find alternative for LivingEntity.getTargetBlock(HashSet<Byte> transparent, int maxDistance)
                    loc.setWorld(player.getWorld());
                } else {
                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + "Specified player" + ChatColor.GRAY + args[3] + ChatColor.WHITE + " isn't online.");
                    return null;
                }
            } else { //args.length >= 6 (and >=7 if ConsoleCommandSender)
                try {
                    x = Double.parseDouble(args[3]);
                    y = Double.parseDouble(args[4]);
                    z = Double.parseDouble(args[5]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + " (" + ChatColor.GRAY + args[3] + ", " + args[4] + ", " + args[5] + ChatColor.WHITE + ") aren't valid (x, y, z) coordinates.");
                    return null;
                }
                if (args.length >= 7) {
                    w = Corruption.in.getServer().getWorld(args[6]);
                    if (w == null) {
                        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.GRAY + args[6] + ChatColor.WHITE + " isn't a valid World.");
                        return null;
                    }
                } else {
                    w = ((BlockCommandSender) sender).getBlock().getWorld();
                }
                loc = new Location(w, x, y, z);
            }
        }
        return loc;
    }

    private static Boolean Spawn(Location location, BossData bossData, int amount) {
        EntityType entityType = bossData.getEntityType();

        //Withers should not be spawned using a command.
        if (Wither.class.isAssignableFrom(entityType.getEntityClass())) {
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.RED + "Withers should not be spawned using a command.\nDoing so will result in unexpected behaviour.");
            return false;
        }

        for (int i = 0; i < amount; i++) {
            if (CorEntityManager.spawnBossEntity(location, entityType, bossData) == null)
                return false;
        }

        return true;
    }


    private static void SendUsage(Map<String, BossData> bossesData) {
        StringBuilder bossListBuilder = new StringBuilder();

        for (String key : bossesData.keySet()) {
            if (bossesData.get(key).getEntityType() == EntityType.WITHER)
                bossListBuilder.append(ChatColor.STRIKETHROUGH + key + ChatColor.RESET + ChatColor.GRAY + ", ");
            else
                bossListBuilder.append(key + ", ");
        }

        bossListBuilder.substring(0, bossListBuilder.length() - 2);

        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.pluginName + "] " + ChatColor.WHITE + "Available Bosses:");
        sender.sendMessage(ChatColor.GRAY + bossListBuilder.toString());
    }
}

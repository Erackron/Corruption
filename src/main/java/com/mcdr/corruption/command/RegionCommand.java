package com.mcdr.corruption.command;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.BossConfig;
import com.mcdr.corruption.config.GlobalConfig;
import com.mcdr.corruption.config.WorldConfig;
import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.entity.Spawner;
import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.util.MathUtil;
import com.mcdr.corruption.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RegionCommand extends BaseCommand {
    public static void process() {
        if (args.length < 2) {
            sendUsage("<list|info|enable|disable|add|remove|edit>");
            return;
        }
        String arg = args[1].toLowerCase();
        if (arg.matches("l(ist)?")) {
            if (checkPermission("cor.spawn.region.list", true))
                list();
        } else if (arg.equals("enable")) {
            if (checkPermission("cor.spawn.region.toggle", true))
                enable();
        } else if (arg.equals("disable")) {
            if (checkPermission("cor.spawn.region.toggle", true))
                disable();
        } else if (arg.matches("i(nfo)?")) {
            if (checkPermission("cor.spawn.region.info", true))
                info();
        } else if (arg.matches("a(dd)?")) {
            if (checkPermission("cor.spawn.region.manage", true))
                add();
        } else if (arg.matches("r(em(ove)?)?")) {
            if (checkPermission("cor.spawn.region.manage", true))
                remove();
        } else if (arg.matches("e(dit)?")) {
            if (checkPermission("cor.spawn.region.edit", true))
                edit();
        }
    }

    private static void list() {
        if (args.length > 2) {
            World world = Bukkit.getWorld(args[2]);
            if (world != null) {
                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawners currently configured in " + args[2] + ": ");
                List<Spawner> spawners = GlobalConfig.getSpawners(world);
                for (Spawner spawner : spawners) {
                    sender.sendMessage(spawner.getId() + ". " + spawner.getName() + " - " + spawner.getWorld().getName() + " (" + (spawner.isEnabled() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.WHITE + ")");
                }
                if (spawners.size() == 0)
                    sender.sendMessage("No spawners configured in " + args[2] + ". Use " + ChatColor.GRAY + "/" + label + " " + args[1] + " add" + ChatColor.WHITE + " to create one.");
            } else {
                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Invalid world: '" + args[2] + "'");
            }
        } else {
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawners currently configured: ");
            Spawner[] spawners = GlobalConfig.getSpawners();
            for (Spawner spawner : spawners) {
                sender.sendMessage(spawner.getId() + ". " + spawner.getName() + " - " + spawner.getWorld().getName() + " (" + (spawner.isEnabled() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.WHITE + ")");
            }
            if (spawners.length == 0)
                sender.sendMessage("No spawners configured. Use " + ChatColor.GRAY + "/" + label + " " + args[1] + " add" + ChatColor.WHITE + " to create one.");
        }
    }

    private static void enable() {
        Spawner spawner = getOneSpawner();
        if (spawner != null)
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawner '" + spawner.getId() + "-" + spawner.getName() + "'" + (spawner.enable() ? " has been enabled." : " is already enabled."));
    }

    private static void disable() {
        Spawner spawner = getOneSpawner();
        if (spawner != null)
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawner '" + spawner.getId() + "-" + spawner.getName() + "'" + (spawner.disable() ? " has been disabled." : " is already disabled."));
    }

    private static void info() {
        ArrayList<Spawner> spawners = getSpawners(args.length > 2 ? args[2] : null);
        if (spawners.size() == 0) {
            if (args.length == 2 && sender instanceof Player) {
                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "No spawner found nearby.");
            } else if (args.length == 2) {
                sendUsage(args[1] + " <id|name>");
            } else {
                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Specified spawner with name/id '" + args[2] + "' doesn't exist.");
            }
        } else {
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawner info:");
            for (Spawner spawner : spawners) {
                Map<String, Object> info = spawner.getInfo();
                for (Map.Entry<String, Object> entry : info.entrySet()) {
                    String out = ChatColor.GRAY + entry.getKey() + ": " + ChatColor.WHITE;
                    if (entry.getKey().equals("spawnable") && entry.getValue() instanceof Map) {
                        for (Map.Entry s : ((Map<?, ?>) entry.getValue()).entrySet()) {
                            if (s.getKey() instanceof BossData && s.getValue() instanceof Integer) {
                                out += "\n " + ((BossData) s.getKey()).getName() + "\n  Chance: " + s.getValue();
                            }
                        }
                    } else if (entry.getKey().equals("spawned") && entry.getValue() instanceof ArrayList) {
                        for (Object o : ((ArrayList) entry.getValue())) {
                            if (o instanceof Boss) {
                                Boss b = ((Boss) o);
                                out += "\n " + b.getRawName() + "\n  Health: " + MathUtil.round(b.getHealth(), 2) + "/" + b.getMaxHealth();
                            }
                        }
                    } else {
                        out += entry.getValue();
                    }
                    sender.sendMessage(out);
                }
                if (sender instanceof Player && spawner.getLocation().getWorld().equals(((Player) sender).getWorld())) {
                    sender.sendMessage(ChatColor.GRAY + "distance: " + ChatColor.WHITE + MathUtil.round(Math.sqrt(MathUtil.getDistanceSquared(spawner.getLocation(), ((Player) sender).getLocation())), 2) + "m");
                }
                sender.sendMessage("----------------------------------");
            }
        }
    }

    private static void add() {
        if (args.length == 2) {
            String locationPart = (sender instanceof Player) ?
                    "[" + ChatColor.GRAY + "x:" + ChatColor.WHITE + "<x> " + ChatColor.GRAY + "y:" + ChatColor.WHITE + "<y> " + ChatColor.GRAY + "z:" + ChatColor.WHITE + "<z>] - The spawn location (default: current location) \n[" + ChatColor.GRAY + "world:" + ChatColor.WHITE + "<world>]] - The spawn world (default: current world)"
                    : ChatColor.GRAY + "x:" + ChatColor.WHITE + "<x> " + ChatColor.GRAY + "y:" + ChatColor.WHITE + "<y> " + ChatColor.GRAY + "z:" + ChatColor.WHITE + "<z> " + ChatColor.GRAY + "world:" + ChatColor.WHITE + "<world> - The spawn location";
            sendUsage(args[1] + " <name>\n" + locationPart + "\n[" + ChatColor.GRAY + "radius:" + ChatColor.WHITE + "<radius>] - The radius of spawning (default: 10)\n[" + ChatColor.GRAY + "interval:" + ChatColor.WHITE + "<interval>] - The interval in seconds between spawns (default: 5)\n[" + ChatColor.GRAY + "cap:" + ChatColor.WHITE + "<spawncap>] - The maximum number of bosses alive (default: 10)");
            sender.sendMessage("This command uses named arguments, so order of properties doesn't matter.\nIf an optional argument is omitted, the default will be used instead.");
        } else {
            HashMap<String, String> arguments = new HashMap<String, String>();
            for (int i = 3; i < args.length; i++) {
                String[] split = args[i].split(":");
                if (split.length == 2) {
                    arguments.put(split[0].toLowerCase(), split[1]);
                } else {
                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Invalid argument: '" + args[i] + "'.");
                    return;
                }
            }
            int id = GlobalConfig.getFirstNewIdAvailable(), x, y, z, radius = 10, spawnIntervalSeconds = 5, spawnCap = 10;
            World world = null;

            if (arguments.containsKey("x") && arguments.containsKey("y") && arguments.containsKey("z")) {
                String cur = "x";
                try {
                    x = Integer.parseInt(arguments.get("x"));
                    cur = "y";
                    y = Integer.parseInt(arguments.get("y"));
                    if (y <= 0 || y > 256) {
                        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Y-coordinate must be between 1 and 256.");
                        return;
                    }
                    cur = "z";
                    z = Integer.parseInt(arguments.get("z"));
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + MessageUtil.ucFirst(cur) + "-coordinate is not an integer: " + arguments.get(cur));
                    return;
                }
            } else if (sender instanceof Player) {
                Location loc = ((Player) sender).getLocation();
                x = loc.getBlockX();
                y = loc.getBlockY();
                z = loc.getBlockZ();
                if (!arguments.containsKey("world"))
                    world = loc.getWorld();
            } else {
                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "No coordinates specified.");
                return;
            }

            if (arguments.containsKey("world")) {
                world = Bukkit.getWorld(arguments.get("world"));
                if (world == null) {
                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "World '" + arguments.get("world") + "' doesn't exist.");
                    return;
                }
            }
            if (world == null) {
                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "No World specified.");
                return;
            }

            if (arguments.containsKey("radius")) {
                try {
                    radius = Integer.parseInt(arguments.get("radius"));
                    if (radius <= 0 || radius > 30) {
                        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Radius must be between 1 and 30.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Radius is not an integer: " + arguments.get("radius"));
                    return;
                }
            }

            if (arguments.containsKey("interval")) {
                try {
                    spawnIntervalSeconds = Integer.parseInt(arguments.get("interval"));
                    if (spawnIntervalSeconds <= 0) {
                        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawninterval must be > 0.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawninterval is not an integer: " + arguments.get("interval"));
                    return;
                }
            }

            if (arguments.containsKey("cap")) {
                try {
                    spawnCap = Integer.parseInt(arguments.get("cap"));
                    if (spawnCap <= 0) {
                        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawncap must be > 0.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawncap is not an integer: " + arguments.get("cap"));
                    return;
                }
            }
            Spawner spawner = new Spawner(args[2], GlobalConfig.getFirstNewIdAvailable(), x, y, z, radius, world, false, spawnIntervalSeconds, spawnCap, new HashMap<BossData, Integer>());
            GlobalConfig.addSpawner(spawner);
            WorldConfig.addSpawner(world, spawner);
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Sucessfully created spawner '" + id + "-" + spawner.getName() + "'.");
            sender.sendMessage("Use " + ChatColor.GRAY + "/" + label + " " + args[0] + " edit " + id + ChatColor.WHITE + " to add bosses,\nand " + ChatColor.GRAY + "/" + label + " " + args[0] + " enable " + id + ChatColor.WHITE + " to enable.");
        }
    }

    private static void remove() {
        Spawner spawner = getOneSpawner();
        if (spawner != null) {
            spawner.disable();
            spawner.purge();
            WorldConfig.removeSpawner(spawner.getWorld(), spawner);
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawner with id '" + spawner.getId() + "' has been deleted.");
        }
    }

    private static void edit() {
        Spawner spawner = getOneSpawner();
        if (spawner != null) {
            if (args.length > 3) {
                if (args[3].toLowerCase().matches("ab|addboss")) {
                    if (args.length > 5) {
                        String bossName = args[4];
                        int chance;
                        try {
                            chance = Integer.parseInt(args[5]);
                            if (chance <= 0) {
                                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawnchance must be > 0");
                                return;
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawnchance is not an integer: " + args[5]);
                            return;
                        }
                        if (BossConfig.getBossesData().containsKey(bossName)) {
                            if (!spawner.hasBoss(bossName)) {
                                spawner.addSpawnable(BossConfig.getBossesData().get(bossName), chance);
                                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Added '" + bossName + "' with a chance of " + chance + " to spawner '" + spawner.getId() + "-" + spawner.getName() + "'.");
                            } else {
                                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawner '" + spawner.getId() + "-" + spawner.getName() + "' already spawns '" + bossName + "'.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "'" + bossName + "' is not a valid Boss.");
                        }
                    } else {
                        sendUsage(args[1] + " " + args[2] + " addBoss <bossName> <spawnChance>");
                    }
                } else if (args[3].toLowerCase().matches("eb|editboss")) {
                    if (args.length > 5) {
                        HashMap<String, String> argsMap = new HashMap<String, String>();
                        String bossNameKey = args[4];
                        if (!spawner.hasBoss(bossNameKey)) {
                            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawner '" + spawner.getId() + "-" + spawner.getName() + "' doesn't spawn '" + bossNameKey + "'.");
                            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Use: " + ChatColor.GRAY + "/" + label + " info " + spawner.getId() + ChatColor.WHITE + " for more info.");
                            return;
                        }
                        String newName;
                        int chance = 0;
                        for (int i = 5; i < args.length; i++) {
                            String[] split = args[i].split(":");
                            if (split.length == 2) {
                                argsMap.put(split[0], split[1]);
                            } else {
                                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Invalid argument: '" + args[i] + "'.");
                                return;
                            }
                        }
                        newName = argsMap.get("name");
                        if (argsMap.containsKey("chance")) {
                            try {
                                chance = Integer.parseInt(argsMap.get("chance"));
                                if (chance <= 0) {
                                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawnchance must be > 0");
                                    return;
                                }
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawnchance is not an integer: " + argsMap.get("chance"));
                                return;
                            }
                        }
                        if (!bossNameKey.equals(newName) && newName != null) { //New boss
                            if (BossConfig.getBossesData().containsKey(newName)) {
                                int c = spawner.removeSpawnable(BossConfig.getBossesData().get(bossNameKey));
                                spawner.addSpawnable(BossConfig.getBossesData().get(newName), chance == 0 ? c : chance);
                                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "'" + bossNameKey + "' changed to '" + newName + "'" + (chance == 0 ? "." : " and set chance to " + chance + "."));
                            } else {
                                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "'" + newName + "' isn't a valid boss.");
                            }
                        } else if (newName == null && chance != 0) {
                            spawner.addSpawnable(BossConfig.getBossesData().get(bossNameKey), chance);
                            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Changed chance of '" + bossNameKey + "' to " + chance + ".");
                        }
                    } else {
                        sendUsage(args[1] + " " + args[2] + " editBoss <bossName> " + ChatColor.GRAY + "chance:" + ChatColor.WHITE + "<spawnChance>");
                        sendUsage(args[1] + " " + args[2] + " editBoss <bossName> " + ChatColor.GRAY + "name:" + ChatColor.WHITE + "<bossName> [" + ChatColor.GRAY + "chance:" + ChatColor.WHITE + "<spawnChance>]");
                    }
                } else if (args[3].toLowerCase().matches("rb|removeboss")) {
                    if (args.length > 4) {
                        String bossName = args[4];
                        if (spawner.hasBoss(bossName)) {
                            spawner.removeSpawnable(BossConfig.getBossesData().get(bossName));
                            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Removed '" + bossName + "' from spawner '" + spawner.getId() + "-" + spawner.getName() + "'.");
                        } else {
                            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawner '" + spawner.getId() + "-" + spawner.getName() + "' doesn't spawn '" + bossName + "'.");
                            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Use: " + ChatColor.GRAY + "/" + label + " info " + spawner.getId() + ChatColor.WHITE + " for more info.");
                        }
                    } else {
                        sendUsage(args[1] + " " + args[2] + " removeBoss <bossName>");
                    }
                } else { //Edit spawner property
                    Map<String, Object> info = spawner.getInfo();
                    String prop = args[3].toLowerCase();
                    if (args.length > 4 && info.containsKey(prop) && !prop.matches("id|spawn(able|ed)|active")) {
                        if (prop.equals("name")) {
                            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + MessageUtil.ucFirst(prop) + " of spawner '" + spawner.getId() + "-" + spawner.getName() + "' set to '" + args[4] + "'.");
                            spawner.setName(args[4]);
                            return;
                        } else if (prop.equals("x")) {
                            try {
                                int x = Integer.parseInt(args[4]);
                                spawner.setX(x);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "X-coordinate is not an integer: " + args[4]);
                                return;
                            }
                        } else if (prop.equals("y")) {
                            try {
                                int y = Integer.parseInt(args[4]);
                                if (y <= 0 || y > 256) {
                                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Y-coordinate must be between 1 and 256.");
                                    return;
                                }
                                spawner.setY(y);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Y-coordinate is not an integer: " + args[4]);
                                return;
                            }
                        } else if (prop.equals("z")) {
                            try {
                                int z = Integer.parseInt(args[4]);
                                spawner.setZ(z);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Z-coordinate is not an integer: " + args[4]);
                                return;
                            }
                        } else if (prop.equals("world")) {
                            World world = Bukkit.getWorld(args[4]);
                            if (world != null) {
                                spawner.setWorld(world);
                            } else {
                                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "World '" + args[4] + "' doesn't exist.");
                                return;
                            }
                        } else if (prop.equals("radius")) {
                            try {
                                int radius = Integer.parseInt(args[4]);
                                if (radius <= 0 || radius > 30) {
                                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Radius must be between 1 and 30.");
                                    return;
                                }
                                spawner.setRadius(radius);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Radius is not an integer: " + args[4]);
                                return;
                            }
                        } else if (prop.equals("enabled")) {
                            if (args[4].toLowerCase().matches("true|false")) {
                                boolean enabled = Boolean.parseBoolean(args[4]);
                                if (enabled) {
                                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawner '" + spawner.getId() + "-" + spawner.getName() + "'" + (spawner.enable() ? " has been enabled." : " is already enabled."));
                                } else {
                                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawner '" + spawner.getId() + "-" + spawner.getName() + "'" + (spawner.disable() ? " has been disabled." : " is already disabled."));
                                }
                                return;
                            } else {
                                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawninterval is not a boolean: " + args[4]);
                                return;
                            }
                        } else if (prop.equals("spawninterval")) {
                            try {
                                int spawnInterval = Integer.parseInt(args[4]);
                                if (spawnInterval <= 0) {
                                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawninterval must be > 0.");
                                    return;
                                }
                                spawner.setSpawnInterval(spawnInterval);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawninterval is not an integer: " + args[4]);
                                return;
                            }
                        } else if (prop.equals("spawncap")) {
                            try {
                                int spawnCap = Integer.parseInt(args[4]);
                                if (spawnCap <= 0) {
                                    sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawncap must be > 0.");
                                    return;
                                }
                                spawner.setSpawnCap(spawnCap);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Spawncap is not an integer: " + args[4]);
                                return;
                            }
                        }
                        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + MessageUtil.ucFirst(prop) + " of spawner '" + spawner.getId() + "-" + spawner.getName() + "' set to '" + args[4] + "'.");
                    } else if (sender instanceof Player && args.length == 4 && args[3].toLowerCase().matches("loc(ation)?")) {
                        spawner.setLocation(((Player) sender).getLocation());
                        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Center of spawner '" + spawner.getId() + "-" + spawner.getName() + "' set to your location.");
                    } else {
                        if (args.length > 4)
                            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Specified property '" + ChatColor.GRAY + args[3] + ChatColor.WHITE + "' doesn't exist.");
                        else
                            sendUsage(args[1] + " " + args[2] + " <property> <value>");
                        sender.sendMessage("Available properties:");
                        for (Map.Entry<String, Object> entry : info.entrySet()) {
                            String out = ChatColor.GRAY + entry.getKey() + ": " + ChatColor.WHITE;
                            if (entry.getKey().matches("id|spawn(able|ed)|active")) {
                                out = null;
                            } else {
                                out += entry.getValue();
                            }
                            if (out != null)
                                sender.sendMessage(out);
                        }
                        if (sender instanceof Player)
                            sender.sendMessage(ChatColor.GRAY + "location" + ChatColor.WHITE + "\nNote: location is a special property because you shouldn't add a value, your current location will be used instead.");
                    }
                }
            } else {
                String id = (args.length > 2) ? " " + args[2] : " <id|name>";
                sendUsage(args[1] + id + " <property> <value>");
                sendUsage(args[1] + id + " addBoss <bossName> <spawnChance>");
                sendUsage(args[1] + id + " removeBoss <bossName>");
                sendUsage(args[1] + id + " editBoss <bossName> " + ChatColor.GRAY + "chance:" + ChatColor.WHITE + "<spawnChance>");
                sendUsage(args[1] + id + " editBoss <bossName> " + ChatColor.GRAY + "name:" + ChatColor.WHITE + "<bossName> [" + ChatColor.GRAY + "chance:" + ChatColor.WHITE + "<spawnChance>]");
            }
        }
    }

    private static Spawner getOneSpawner() {
        ArrayList<Spawner> spawnerList = getSpawners(args.length > 2 ? args[2] : null);
        if (spawnerList.size() == 0) {
            if (args.length == 2 && sender instanceof Player) {
                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "No spawner found nearby.");
            } else if (args.length == 2) {
                sendUsage(args[1] + " <id|name>");
            } else {
                sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Specified spawner with name/id '" + args[2] + "' doesn't exist.");
            }
        } else if (spawnerList.size() == 1) {
            return spawnerList.get(0);
        } else {
            sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Multiple spawners found, try again using one of the following ids:");
            for (Spawner spawner : spawnerList) {
                sender.sendMessage(spawner.getName() + "\n id: " + spawner.getId());
            }
        }
        return null;
    }

    private static ArrayList<Spawner> getSpawners(String arg) {
        ArrayList<Spawner> spawners = new ArrayList<Spawner>();
        if (arg != null) {
            try {
                int id = Integer.parseInt(args[2]);
                spawners.add(GlobalConfig.getSpawner(id));
            } catch (NumberFormatException e) {
                spawners = GlobalConfig.getSpawners(args[2]);
            }
        } else if (sender instanceof Player) {
            Player player = ((Player) sender);
            for (Spawner spawner : GlobalConfig.getSpawners(player.getWorld())) {
                if (spawner.isInArea(player.getLocation())) {
                    spawners.add(spawner);
                }
            }
        }
        return spawners;
    }

    private static void sendUsage(String msg) {
        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.GRAY + "Usage: " + ChatColor.WHITE + "/" + label + " " + args[0] + " " + msg);
    }
}
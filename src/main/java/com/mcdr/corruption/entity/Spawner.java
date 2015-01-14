package com.mcdr.corruption.entity;


import com.mcdr.corruption.config.WorldConfig;
import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.task.SpawnManager;
import com.mcdr.corruption.util.MathUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.Map.Entry;

public class Spawner {
    private String name;
    private int id;
    private long spawnInterval;
    private Location spawnpoint;
    private int radius;
    private Map<BossData, Integer> spawnable;
    private final List<Boss> spawned;
    private boolean enabled = true;
    private int spawnCap;
    private int maxSpawnChance = 0;

    public Spawner(String name, int id, int x, int y, int z, int radius, World world, boolean enabled, int spawnIntervalSeconds, int spawnCap, Map<BossData, Integer> spawnable) {
        this.spawnCap = spawnCap;
        spawned = Collections.synchronizedList(new ArrayList<Boss>());
        this.name = name;
        this.id = id;
        this.radius = radius;
        this.spawnInterval = (long) (spawnIntervalSeconds * 20.0D);
        this.spawnpoint = new Location(world, x, y, z);
        this.spawnable = spawnable;
        this.enabled = enabled;

        for (int chance : spawnable.values()) {
            maxSpawnChance += chance;
        }
    }

    public boolean enable() {
        if (!enabled) {
            enabled = true;
            SpawnManager.registerSpawner(this);
            WorldConfig.updateSpawner(getWorld(), this, true);
            return true;
        }
        return false;
    }

    public boolean disable() {
        if (enabled) {
            enabled = false;
            SpawnManager.deregisterSpawner(this);
            WorldConfig.updateSpawner(getWorld(), this, false);
            return true;
        }
        return false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean shouldStart() {
        return enabled && spawnpoint.getChunk().isLoaded();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void restoreBoss(Boss boss) {
        spawned.add(boss);
    }

    public void removeBoss(Boss boss) {
        spawned.remove(boss);
    }

    public synchronized boolean hasBoss(String bossName) {
        for (BossData bossData : spawnable.keySet()) {
            if (bossData.getName().equals(bossName))
                return true;
        }
        return false;
    }

    public synchronized void addSpawnable(BossData bossData, int chance) {
        Integer prevChance = spawnable.put(bossData, chance);
        int chanceDelta = chance;
        if (prevChance != null) {
            chanceDelta -= prevChance;
        }
        maxSpawnChance += chanceDelta;
        WorldConfig.updateSpawner(getWorld(), this, bossData.getName(), chance);
    }

    public synchronized int removeSpawnable(BossData bossData) {
        Integer prevChance = spawnable.remove(bossData);
        if (prevChance != null) {
            maxSpawnChance -= prevChance;
            WorldConfig.updateSpawnerRemoveBoss(getWorld(), this, bossData.getName());
            return prevChance;
        }
        return 0;
    }

    public boolean isInArea(Location loc) {
        return MathUtil.isNear(spawnpoint, loc, 0, radius);
    }

    public List<Block> findValidBlocks(int width, int height) {
        List<Block> validBlocks = new ArrayList<Block>();
        World world = spawnpoint.getWorld();
        Location location = spawnpoint.clone();
        Vector direction = new Vector(1.0, 0.0, 0.0);
        int maxTurns = radius * 4 + 1;
        Block nextBlock;

        for (int turn = 0; turn < maxTurns; turn++) {
            int blocksInLine = turn / 2 + 1;

            if (turn == maxTurns - 1)
                blocksInLine--;

            for (int currentBlock = 0; currentBlock < blocksInLine; currentBlock++) {
                Block block = world.getBlockAt(location.add(direction));

                if (isValid(block, width, height)) {
                    validBlocks.add(block);
                } else {
                    Location blockLocation = block.getLocation();
                    double minY = blockLocation.getY() - radius;
                    double maxY = blockLocation.getY() + radius;

                    for (double i = minY; i < maxY; i++) {
                        blockLocation.setY(i);

                        nextBlock = world.getBlockAt(blockLocation);

                        if (isValid(nextBlock, width, height)) {
                            validBlocks.add(nextBlock);
                            break;
                        }
                    }
                }
            }

            direction = new Vector(-direction.getZ(), 0.0, direction.getX());
        }

        return validBlocks;
    }

    private boolean isValid(Block b, int width, int height) {
        if (!b.isEmpty() || !isSafe(b.getRelative(BlockFace.DOWN)))
            return false;

        Block temp = b.getRelative(-width / 2, 0, -width / 2);
        for (int i = 1; i <= width; i++) {
            for (int j = 1; j <= width; j++) {
                if (!hasVerticalSpace(temp.getRelative(BlockFace.SOUTH, j), height))
                    return false;
            }
            temp = temp.getRelative(BlockFace.EAST);
        }
        return true;
    }

    private boolean isSafe(Block block) {
        return block.getType().isSolid();
    }

    private boolean hasVerticalSpace(Block base, int minAvailableHeight) {
        for (int i = 0; i < minAvailableHeight; i++) {
            if (!base.isEmpty())
                return false;
            base = base.getRelative(BlockFace.UP);
        }
        return true;
    }

    public long getSpawnInterval() {
        return spawnInterval;
    }

    public synchronized void spawn() {
        synchronized (spawned) {
            List<Boss> allBosses = CorEntityManager.getBosses();
            for (Iterator<Boss> it = spawned.iterator(); it.hasNext(); ) {
                if (!allBosses.contains(it.next()))
                    it.remove();
            }
        }

        if (!shouldStart()) {
            SpawnManager.stop(id);
            return;
        }

        if (spawned.size() < spawnCap) {
            int random = MathUtil.Random(0, maxSpawnChance), curChance = 0;
            for (Entry<BossData, Integer> entry : spawnable.entrySet()) {
                curChance += entry.getValue();
                if (curChance > random) {
                    BossData bossData = entry.getKey();
                    int[] dimensions = MathUtil.getDimensions(bossData);
                    List<Block> validBlocks = findValidBlocks(dimensions[0], dimensions[1]);
                    if (validBlocks != null) {
                        Boss boss = CorEntityManager.spawnBossEntity(validBlocks.get(MathUtil.Random(0, validBlocks.size() - 1)).getLocation(), bossData.getEntityType(), bossData, this);
                        if (boss != null) {
                            spawned.add(boss);
                        }
                    }
                    break;
                }
            }
        }
    }

    public World getWorld() {
        return spawnpoint.getWorld();
    }

    public Chunk getCenterChunk() {
        return spawnpoint.getChunk();
    }

    public Location getLocation() {
        return spawnpoint.clone();
    }

    public synchronized Map<String, Object> getInfo() {
        HashMap<String, Object> info = new LinkedHashMap<String, Object>();
        info.put("name", name);
        info.put("id", id);
        info.put("x", spawnpoint.getBlockX());
        info.put("y", spawnpoint.getBlockY());
        info.put("z", spawnpoint.getBlockZ());
        info.put("world", spawnpoint.getWorld().getName());
        info.put("radius", radius);
        info.put("enabled", enabled);
        info.put("active", shouldStart());
        info.put("spawninterval", MathUtil.round(spawnInterval / 20, 0) + "s");
        info.put("spawncap", spawnCap);
        info.put("spawnable", new HashMap<BossData, Integer>(spawnable));
        info.put("spawned", new ArrayList<Boss>(spawned));
        return info;
    }

    public synchronized void purge() {
        CorEntityManager.purgeBosses(spawned);
        spawned.clear();
    }

    // Setters: should only be used through the RegionCommand's edit command.
    public void setName(String name) {
        if (name != null) {
            WorldConfig.updateSpawnerProperty(getWorld(), this, "name", name);
            this.name = name;
        }
    }

    public void setX(int x) {
        this.spawnpoint.setX(x);
        WorldConfig.updateSpawnerProperty(getWorld(), this, "X", x);
    }

    public void setY(int y) {
        if (y > 0 && y <= 256) {
            this.spawnpoint.setY(y);
            WorldConfig.updateSpawnerProperty(getWorld(), this, "Y", y);
        }
    }

    public void setZ(int z) {
        this.spawnpoint.setZ(z);
        WorldConfig.updateSpawnerProperty(getWorld(), this, "Z", z);
    }

    public void setWorld(World world) {
        if (world != null && !this.spawnpoint.getWorld().equals(world)) {
            WorldConfig.updateSpawnerProperty(getWorld(), this, "world", world.getName());
            this.spawnpoint.setWorld(world);
        }
    }

    public void setLocation(Location location) {
        if (location != null) {
            setWorld(location.getWorld());
            if (spawnpoint.getBlockX() != location.getBlockX()) {
                setX(location.getBlockX());
            }
            if (spawnpoint.getBlockY() != location.getBlockY()) {
                setY(location.getBlockY());
            }
            if (spawnpoint.getBlockZ() != location.getBlockZ()) {
                setZ(location.getBlockZ());
            }
        }
    }

    public void setRadius(int radius) {
        if (radius > 0 || radius <= 30) {
            this.radius = radius;
            WorldConfig.updateSpawnerProperty(getWorld(), this, "Radius", radius);
        }
    }

    public void setSpawnInterval(int intervalSeconds) {
        if (intervalSeconds > 0) {
            this.spawnInterval = (long) (intervalSeconds * 20.0D);
            WorldConfig.updateSpawnerProperty(getWorld(), this, "SpawnInterval", intervalSeconds);
        }
    }

    public void setSpawnCap(int spawnCap) {
        if (spawnCap > 0) {
            this.spawnCap = spawnCap;
            WorldConfig.updateSpawnerProperty(getWorld(), this, "SpawnCap", spawnCap);
        }
    }
}

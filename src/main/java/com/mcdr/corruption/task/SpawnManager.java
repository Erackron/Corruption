package com.mcdr.corruption.task;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.GlobalConfig;
import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.entity.Spawner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class SpawnManager {
    private static HashMap<Integer, SpawnerTask> spawners = new HashMap<Integer, SpawnerTask>(),
            inactive = new HashMap<Integer, SpawnerTask>();

    public static void initialize(Spawner... spawners) {
        if (spawners == null)
            return;

        for (Spawner spawner : spawners) {
            if (spawner.isEnabled()) {
                inactive.put(spawner.getId(), new SpawnerTask(spawner));
            }
        }
    }

    public static void registerSpawner(Spawner spawner) {
        SpawnerTask task = new SpawnerTask(spawner);
        if (spawner.shouldStart()) {
            spawners.put(spawner.getId(), task);
            try {
                task.getTaskId();
            } catch (IllegalStateException e) {
                task.runTaskTimerAsynchronously(Corruption.getInstance(), spawner.getSpawnInterval(), spawner.getSpawnInterval());
            }
        } else if (spawner.isEnabled()) {
            inactive.put(spawner.getId(), task);
        }
    }

    public static void deregisterSpawner(Spawner spawner) {
        if (spawners.containsKey(spawner.getId())) {
            try {
                spawners.remove(spawner.getId()).cancel();
            } catch (IllegalStateException ignored) {
            }
        } else if (inactive.containsKey(spawner.getId())) {
            inactive.remove(spawner.getId());
        }
    }

    public static boolean start(int id) {
        if (inactive.containsKey(id) && inactive.get(id).getSpawner().shouldStart()) {
            SpawnerTask task = inactive.remove(id);
            spawners.put(id, task);
            task.runTaskTimerAsynchronously(Corruption.getInstance(), task.getSpawner().getSpawnInterval(), task.getSpawner().getSpawnInterval());
            return true;
        }
        return false;
    }

    public static boolean stop(int id) {
        if (spawners.containsKey(id)) {
            SpawnerTask task = spawners.remove(id);
            inactive.put(id, task);
            task.cancel();
            return true;
        }
        return false;
    }

    public static void stopAll() {
        for (Map.Entry<Integer, SpawnerTask> entry : spawners.entrySet()) {
            entry.getValue().cancel();
        }
        inactive.putAll(spawners);
        spawners.clear();
    }

    public static void startAll() {
        Map.Entry<Integer, SpawnerTask> cur;
        long spawnTime;
        Iterator<Map.Entry<Integer, SpawnerTask>> it = inactive.entrySet().iterator();
        while (it.hasNext()) {
            cur = it.next();
            if (cur.getValue().getSpawner().shouldStart()) {
                spawnTime = cur.getValue().getSpawner().getSpawnInterval();
                try {
                    cur.getValue().runTaskTimerAsynchronously(Corruption.getInstance(), spawnTime, spawnTime);
                } catch (IllegalStateException ignored) {
                } // Already running somehow
                it.remove();
                spawners.put(cur.getKey(), cur.getValue());
            }
        }
    }

    public static void reload() {
        Map<Integer, Spawner> spawnerMap = GlobalConfig.getSpawnerMap();
        for (Iterator<Integer> it = inactive.keySet().iterator(); it.hasNext(); ) {
            int id = it.next();
            if (spawnerMap.containsKey(id)) {
                inactive.get(id).setSpawner(spawnerMap.get(id));
            } else {
                it.remove();
            }
        }

        for (Boss boss : CorEntityManager.getBosses()) {
            if (boss.getSpawner() != 0) {
                GlobalConfig.getSpawner(boss.getSpawner()).restoreBoss(boss);
            }
        }
    }
}

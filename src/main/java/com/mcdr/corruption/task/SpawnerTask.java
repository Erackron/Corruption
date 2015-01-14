package com.mcdr.corruption.task;

import com.mcdr.corruption.entity.Spawner;

public class SpawnerTask extends BaseTask {
    private Spawner spawner;

    public SpawnerTask(Spawner spawner) {
        this.spawner = spawner;
    }

    public Spawner getSpawner() {
        return spawner;
    }

    protected void setSpawner(Spawner spawner) {
        this.spawner = spawner;
    }

    @Override
    public void run() {
        spawner.spawn();
    }
}

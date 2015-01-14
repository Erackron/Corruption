package com.mcdr.corruption.listener;

import com.mcdr.corruption.CorruptionAPI;
import com.mcdr.corruption.config.BossConfig;
import com.mcdr.corruption.config.GlobalConfig;
import com.mcdr.corruption.config.WorldConfig;
import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.entity.Spawner;
import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.task.SpawnManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;


public class CorWorldListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldLoad(WorldLoadEvent event) throws Exception {
        WorldConfig.Load(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        WorldConfig.remove(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        Entity[] eA = event.getChunk().getEntities();
        for (Entity e : eA) {
            if (CorruptionAPI.hasBossMetatag(e) && CorEntityManager.getBoss(e) == null && e instanceof LivingEntity) {
                BossData bossData = BossConfig.getBossesData().get(CorruptionAPI.getBossMetatag(e));
                if (bossData != null) {
                    CorEntityManager.addBoss(Boss.restoreBoss((LivingEntity) e, bossData));
                } else {
                    e.remove();
                }
            }
        }

        for (Spawner spawner : GlobalConfig.getSpawners(event.getWorld())) {
            if (spawner.shouldStart() && spawner.getCenterChunk().equals(event.getChunk())) {
                SpawnManager.start(spawner.getId());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Spawner spawner : GlobalConfig.getSpawners(event.getWorld())) {
            if (spawner.shouldStart() && spawner.getCenterChunk().equals(event.getChunk())) {
                SpawnManager.stop(spawner.getId());
            }
        }
    }

}

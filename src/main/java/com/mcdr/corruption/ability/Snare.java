package com.mcdr.corruption.ability;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.entity.Boss;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;

public class Snare extends Ability implements Listener {
    private int duration;
    private boolean destructible;
    private int radius;

    private boolean isRunning = false;
    private List<Block> blocks = new ArrayList<Block>();

    public Snare clone() {
        Snare snare = new Snare();
        copySettings(snare);
        snare.setDuration(this.duration);
        snare.setDestructible(this.destructible);
        snare.setRadius(this.radius);
        //Register events, since this ability uses a BlockBreakEvent listener
        Bukkit.getServer().getPluginManager().registerEvents(snare, Corruption.getInstance());
        return snare;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isDestructible() {
        return destructible;
    }

    public void setDestructible(boolean destructible) {
        this.destructible = destructible;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * OnDeath Execute
     */
    public boolean Execute(LivingEntity livingEntity, Location lastLoc, Boss boss) {
        if (!super.Execute(livingEntity, lastLoc, boss))
            return false;

        ensnare(livingEntity);

        sendAreaMessage(lastLoc, boss.getName(), livingEntity);
        return true;
    }

    /**
     * Normal Execute
     */
    public boolean Execute(LivingEntity livingEntity, Boss boss) {
        if (!super.Execute(livingEntity, boss))
            return false;

        if (!ensnare(livingEntity))
            return false;

        useCooldown(boss);
        sendAreaMessage(boss, livingEntity);
        return true;
    }

    private boolean ensnare(LivingEntity livingEntity) {
        blocks = findValidBlocks(livingEntity.getLocation(), 0, radius);
        if (blocks.isEmpty())
            return false;

        isRunning = true;

        for (Block b : blocks)
            b.setType(Material.WEB);

        Corruption.scheduler.scheduleSyncDelayedTask(Corruption.getInstance(), new CleanUp(blocks, this), duration);

        if (!destructible) {
            Bukkit.getServer().getPluginManager().registerEvents(this, Corruption.getInstance());
        }
        return true;
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (isRunning) {
            if (!destructible || blocks.contains(event.getBlock()))
                event.setCancelled(true);
        }
    }

    class CleanUp implements Runnable {
        private List<Block> blocks;
        private Snare listener;

        public CleanUp(List<Block> blocks, Snare listener) {
            this.blocks = blocks;
            this.listener = listener;
        }

        public void run() {
            for (Block b : blocks) {
                if (b.getType() == Material.WEB)
                    b.setType(Material.AIR);
            }
            isRunning = false;
            if (!destructible)
                HandlerList.unregisterAll(listener);
            blocks.clear();
        }

    }

    /**
     * Unregister this ability from the BlockBreakEvent if this Snare instance is no longer bound to a boss.
     * Called by the Java Garbage collector.
     */
    public void finalize() throws Throwable {
        super.finalize();
        BlockBreakEvent.getHandlerList().unregister(this);
    }
}

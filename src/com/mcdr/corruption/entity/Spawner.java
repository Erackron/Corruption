package com.mcdr.corruption.entity;


import com.mcdr.corruption.entity.data.BossData;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Spawner {
    private World world;
    private int[] bottomLeft, topRight;
    private List<BossData> bosses;
    private Random rand;

    public Spawner(int x1, int y1, int z1, int x2, int y2, int z2, World world, BossData... bosses) {
        if (world == null)
            return;
        int temp;
        if (x2 < x1) {
            temp = x1;
            x1 = x2;
            x2 = temp;
        }
        if (y2 < y1) {
            temp = y1;
            y1 = y2;
            y2 = temp;
        }
        if (z2 < z1) {
            temp = z1;
            z1 = z2;
            z2 = temp;
        }
        this.bottomLeft = new int[]{x1, y1, z1};
        this.topRight = new int[]{x2, y2, z2};
        this.world = world;
        this.bosses = Arrays.asList(bosses);
        this.rand = new Random();
    }

    /*public double[] getBottomLeft() {
        return bottomLeft;
    }

    public double[] getTopRight() {
        return topRight;
    }*/

    public boolean fallsInRange(Location loc) {
        if (loc == null || !world.equals(loc.getWorld()))
            return false;

        if (bottomLeft[0] <= loc.getBlockX() && loc.getBlockX() <= topRight[0]) {
            if (bottomLeft[1] <= loc.getBlockY() && loc.getBlockY() <= topRight[1]) {
                if (bottomLeft[2] <= loc.getBlockZ() && loc.getBlockZ() <= topRight[2]) {
                    return true;
                }
            }
        }
        return false;
    }

    public Location randomSpawnLocation() {
        return randomSpawnLocation(2);
    }

    public Location randomSpawnLocation(int minAvailableBlocks) {
        return null; //TODO Implement
    }
}

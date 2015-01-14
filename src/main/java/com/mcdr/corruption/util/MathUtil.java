package com.mcdr.corruption.util;

import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.entity.data.SkeletonBossData;
import com.mcdr.corruption.entity.data.SlimeBossData;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class is used to group utility methods that have to do with math.
 */
public abstract class MathUtil {
    public static java.util.Random random = new java.util.Random();

    protected static double round(double value) {
        return round(value, 1);
    }

    public static double round(double value, int places) {
        if (places < 0)
            places = 1;
        if (value < 0.0)
            return 0.0;

        BigDecimal bd;
        try {
            bd = new BigDecimal(value);
            bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
        } catch (NumberFormatException ex) {
            bd = new BigDecimal(0.0);
        }
        return bd.doubleValue();
    }

    public static int Random(int min, int max) {
        return (int) (random.nextDouble() * (max - min + 1) + min);
    }

    public static boolean isNear(Location first, Location second, int minDistance, int maxDistance) {
        if (first.getWorld() != second.getWorld())
            return false;

        double dist = getDistanceSquared(first, second);

        return dist < maxDistance * maxDistance && dist > minDistance * minDistance;

    }

    public static double getDistanceSquared(Location first, Location second) {
        if (first.getWorld() != second.getWorld())
            return 0;
        double relX = first.getX() - second.getX();
        double relY = first.getY() - second.getY();
        double relZ = first.getZ() - second.getZ();
        return relX * relX + relY * relY + relZ * relZ;
    }

    public static void setFacing(LivingEntity le, Location targetLoc) {
        if (!le.isValid() || targetLoc == null)
            return;
        setFacing(le, targetLoc.clone().add(.5, .0, .5).subtract(le.getEyeLocation()).toVector().normalize());
    }

    public static void setFacing(LivingEntity le, Vector vector) {
        double yaw = Math.toDegrees(Math.atan2(-vector.getX(), vector.getZ()));
        double pitch = Math.toDegrees(-Math.asin(vector.getY()));

        Location loc = le.getLocation();
        loc.setYaw((float) yaw);
        loc.setPitch((float) pitch);

        le.teleport(loc);
    }

    public static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> sortEntriesByValues(Map<K, V> map, boolean ascendant) {
        SortedSet<Map.Entry<K, V>> sortedEntries;
        if (ascendant)
            sortedEntries = new TreeSet<Map.Entry<K, V>>(new ValueComparatorAsc<K, V>());
        else
            sortedEntries = new TreeSet<Map.Entry<K, V>>(new ValueComparatorDesc<K, V>());
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    /**
     * Get an array of dimensions for a given BossData mob.
     *
     * @param bossData The BossData object to get the mob dimensions of
     * @return The mob's dimensions
     */
    public static int[] getDimensions(BossData bossData) {
        EntityType e = bossData.getEntityType();
        switch (e) {
            case SKELETON:
                if (((SkeletonBossData) bossData).getSkeletonType() == Skeleton.SkeletonType.WITHER)
                    return new int[]{1, 3};
            case SHEEP:
            case BLAZE:
            case COW:
            case MUSHROOM_COW:
            case CREEPER:
            case ZOMBIE:
            case PIG_ZOMBIE:
            case SNOWMAN:
            case WITCH:
            case VILLAGER:
                return new int[]{1, 2};

            case GHAST:
                return new int[]{4, 4};
            case ENDERMAN:
                return new int[]{1, 3};
            case WITHER:
                return new int[]{3, 3};
            case HORSE:
                return new int[]{3, 2};
            case GIANT:
                return new int[]{1, 12}; //Actual dimensions are larger(5-6), but since it's a big performance penalty to increase the width, 1 block as width is better.
            case SPIDER:
                return new int[]{2, 1};
            case IRON_GOLEM:
                return new int[]{2, 3};
            case SLIME:
            case MAGMA_CUBE:
                int maxSize = ((SlimeBossData) bossData).getMaximumSize();
                return new int[]{maxSize, maxSize};
            default: //Sane defaults to fit most mobs not added here yet.
                return new int[]{1, 2};
        }
    }

    private static class ValueComparatorAsc<K, V extends Comparable<? super V>> implements Comparator<Map.Entry<K, V>> {
        @Override
        public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
            int res = e1.getValue().compareTo(e2.getValue());
            return res != 0 ? res : 1;
        }
    }

    private static class ValueComparatorDesc<K, V extends Comparable<? super V>> implements Comparator<Map.Entry<K, V>> {
        @Override
        public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
            int res = e2.getValue().compareTo(e1.getValue());
            return res != 0 ? res : 1;
        }
    }
}

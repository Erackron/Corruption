package com.mcdr.corruption.util;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.entity.data.SkeletonBossData;
import com.mcdr.corruption.entity.data.SlimeBossData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.util.Vector;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;


public abstract class Utility {
    public static java.util.Random random = new java.util.Random();

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

    public static boolean hasPermission(CommandSender sender, String permission) {
        return !(sender instanceof Player) || sender.isOp() || hasPermission((Player) sender, permission);
    }

    public static boolean hasPermission(Player player, String permission) {
        return Corruption.in.pm.hasPermission(player, permission);
    }


    public static String parseMessage(String msg, Boss boss) {
        return parseMessage(msg, boss, 0.0);
    }

    public static String parseMessage(String msg, Boss boss, double damage) {
        return parseMessage(msg, boss.getBossData().getName(), boss.getHealth(), boss.getMaxHealth(), damage);
    }

    public static String parseMessage(String msg, String bossName) {
        return parseMessage(msg, bossName, 0.0, 0.0, 0.0);
    }

    public static String parseMessage(String msg, String bossName, double health, double maxHealth, double damage) {
        bossName = (bossName.contains("#")) ? bossName.split("#")[0] : bossName;
        String[] bNameS = bossName.split("(?=\\p{Upper})");
        if (bNameS.length > 1) {
            bossName = bNameS[1];
            for (int i = 2; i < bNameS.length; i++)
                bossName += " " + bNameS[i];
        }
        String healthBar = "";
        if (maxHealth != 0 && maxHealth >= health) {
            if (msg.trim().equalsIgnoreCase("{HEALTHBAR}") && msg.length() < 40) {
                msg = "     {HEALTHBAR}";
            }
            healthBar += "&r[&a";
            int ratio = (int) ((20 * health) / maxHealth);
            boolean red = false;
            for (int i = 0; i < 20; i++) {
                if (!red && i > ratio) {
                    healthBar += "&4";
                    red = true;
                }
                healthBar += "|";
            }
            healthBar += "&r]";
        }


        return ChatColor.translateAlternateColorCodes('&', msg.replace("{BOSSNAME}", bossName).replace(
                "{HEALTH}",
                "" + round(health)
        ).replace(
                "{DAMAGE}",
                "" + round(damage)
        ).replace(
                "{MAXHEALTH}",
                "" + round(maxHealth)
        ).replace(
                "{HEALTHBAR}",
                healthBar
        ));
    }

    private static class ValueComparatorAsc<K, V extends Comparable<? super V>> implements Comparator<Entry<K, V>> {
        @Override
        public int compare(Entry<K, V> e1, Entry<K, V> e2) {
            int res = e1.getValue().compareTo(e2.getValue());
            return res != 0 ? res : 1;
        }
    }

    private static class ValueComparatorDesc<K, V extends Comparable<? super V>> implements Comparator<Entry<K, V>> {
        @Override
        public int compare(Entry<K, V> e1, Entry<K, V> e2) {
            int res = e2.getValue().compareTo(e1.getValue());
            return res != 0 ? res : 1;
        }
    }

    public static <K, V extends Comparable<? super V>> SortedSet<Entry<K, V>> sortEntriesByValues(Map<K, V> map, boolean ascendant) {
        SortedSet<Map.Entry<K, V>> sortedEntries;
        if (ascendant)
            sortedEntries = new TreeSet<Entry<K, V>>(new ValueComparatorAsc<K, V>());
        else
            sortedEntries = new TreeSet<Entry<K, V>>(new ValueComparatorDesc<K, V>());
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    public static void streamToFile(InputStream resource, File file) throws Exception {
        if (!file.exists())
            file.createNewFile();

        OutputStream outputStream = new FileOutputStream(file);

        copy(resource, outputStream);
    }

    public static void fileToFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(sourceFile);
            fos = new FileOutputStream(destFile);
            source = fis.getChannel();
            destination = fos.getChannel();

            // previous code: destination.transferFrom(source, 0, source.size());
            // to avoid infinite loops, should be:
            long count = 0;
            long size = source.size();
            while ((count += destination.transferFrom(source, count, size - count)) < size) ;
        } finally {
            if (source != null)
                source.close();
            if (destination != null)
                destination.close();
            if (fis != null)
                fis.close();
            if (fos != null)
                fos.close();
        }
    }

    private static void copy(InputStream inputStream, OutputStream outputStream) throws Exception {
        int read;
        byte[] bytes = new byte[1024];

        while ((read = inputStream.read(bytes)) != -1)
            outputStream.write(bytes, 0, read);

        inputStream.close();
        outputStream.close();
    }

    /**
     * Checks if the first input is an older version number than the second input
     *
     * @param version1 the version to check
     * @param version2 the version to check against
     * @return true if the first input is an older version number, false if it isn't
     */
    public static boolean isOlderVersion(String version1, String version2) {
        return isNewerVersion(version2, version1);
    }

    /**
     * Checks if the first input is a newer version number than the second input
     *
     * @param version1 the version to check
     * @param version2 the version to check against
     * @return true if the first input is a newer version number, false if it isn't
     */
    public static boolean isNewerVersion(String version1, String version2) {
        String s1 = normalisedVersion(version1);
        String s2 = normalisedVersion(version2);
        int cmp = s1.compareTo(s2);
        //String cmpStr = cmp < 0 ? "<" : cmp > 0 ? ">" : "==";
        return cmp > 0;
    }

    private static String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

    private static String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }

    public static String calculateMd5Hash(File f) throws RuntimeException {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            InputStream is = new FileInputStream(f);
            byte[] buffer = new byte[8192];
            int read;
            try {
                while ((read = is.read(buffer)) > 0) {
                    digest.update(buffer, 0, read);
                }
                byte[] md5sum = digest.digest();
                BigInteger bigInt = new BigInteger(1, md5sum);
                return bigInt.toString(16);
            } catch (IOException e) {
                throw new RuntimeException("Unable to process file for MD5", e);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
                }
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("The md5 algorithm doesn't seem to be available on your system", e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("The file to hash was not found, are you sure you have the right File object?", e);
        }
    }

    private static double round(double value) {
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
            default:
                return new int[]{1, 1};
        }
    }

    public static String ucFirst(String str) {
        if (str == null)
            return "";
        return (str.length() > 0 ? Character.toUpperCase(str.charAt(0)) : "") + (str.length() > 1 ? str.substring(1) : "");
    }
}

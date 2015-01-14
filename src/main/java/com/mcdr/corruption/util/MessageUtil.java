package com.mcdr.corruption.util;

import com.mcdr.corruption.entity.Boss;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * This class is used to group utility methods that have to do with sending/formatting messages.
 */
public abstract class MessageUtil {
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
                "" + MathUtil.round(health)
        ).replace(
                "{DAMAGE}",
                "" + MathUtil.round(damage)
        ).replace(
                "{MAXHEALTH}",
                "" + MathUtil.round(maxHealth)
        ).replace(
                "{HEALTHBAR}",
                healthBar
        ));
    }

    public static void sendMessage(CommandSender sender, String message) {

    }

    public static void sendMessage(CommandSender sender, String messageFormat, ChatColor pluginColor, ChatColor textColor, Object... formatArgs) {

    }

    /**
     * Uppercase the first character of a string.
     * The converting to uppercase is done with a call to Character.toUpperCase()
     * <b>Note:</b> doesn't change the case of any other character
     *
     * @param str The string to uppercase the first character of
     * @return The string with the first character turned uppercase.
     * @see Character#toUpperCase(char)
     */
    public static String ucFirst(String str) {
        if (str == null)
            return "";
        return (str.length() > 0 ? Character.toUpperCase(str.charAt(0)) : "") + (str.length() > 1 ? str.substring(1) : "");
    }
}

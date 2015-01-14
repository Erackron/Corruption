package com.mcdr.corruption.handler;

import com.gmail.nossr50.api.ExperienceAPI;
import com.mcdr.corruption.entity.Boss;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class mcMMOHandler {

    public static void addMeleeXP(Player p, Boss boss) {

        ItemStack weapon = p.getItemInHand();

        switch (weapon.getType()) {
            case AIR:
                ExperienceAPI.addXP(p, "unarmed", boss.getBossData().getMCMMOXPBonus());
                break;
            case IRON_SWORD:
            case WOOD_SWORD:
            case STONE_SWORD:
            case DIAMOND_SWORD:
            case GOLD_SWORD:
                ExperienceAPI.addXP(p, "swords", boss.getBossData().getMCMMOXPBonus());
                break;
            case WOOD_AXE:
            case STONE_AXE:
            case IRON_AXE:
            case DIAMOND_AXE:
            case GOLD_AXE:
                ExperienceAPI.addXP(p, "axes", boss.getBossData().getMCMMOXPBonus());
                break;
            default:
                break;
        }

    }

    public static void addRangeXP(Player p, Boss boss) {
        ExperienceAPI.addXP(p, "archery", boss.getBossData().getMCMMOXPBonus());
    }
}

package com.mcdr.corruption.handler;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass.ExperienceType;
import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.player.CorPlayer;
import org.bukkit.entity.LivingEntity;

public class HeroesHandler {

    private static Heroes plugin;

    public static boolean prepare() {
        plugin = (Heroes) Corruption.getInstance().getServer().getPluginManager().getPlugin("Heroes");
        return plugin != null;
    }

    public static void addXP(CorPlayer corPlayer, double xp, LivingEntity victim) {
        if (xp == 0)
            return;

        Hero hero = plugin.getCharacterManager().getHero(corPlayer.getPlayer());

        ExperienceType type = ExperienceType.KILLING;
        if (hero.hasParty()) {
            hero.getParty().gainExp(xp, type, victim.getLocation());
        } else if (hero.canGain(type)) {
            hero.gainExp(xp, type, victim.getLocation());
        }
    }

}

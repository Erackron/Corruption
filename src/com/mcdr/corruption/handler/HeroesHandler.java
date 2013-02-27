package com.mcdr.corruption.handler;

import org.bukkit.entity.LivingEntity;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import com.herocraftonline.heroes.characters.classes.HeroClass.ExperienceType;
import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.player.CorPlayer;

public class HeroesHandler {
	
	private static Heroes plugin;
	
	public static boolean prepare(){
		plugin = (Heroes) Corruption.in.getServer().getPluginManager().getPlugin("Heroes");
		if(plugin == null)
			return false;
		return true;
	}
	
	public static void addXP(CorPlayer corPlayer, double xp, LivingEntity victim){
		if(xp==0)
			return;
		
		Hero hero = plugin.getCharacterManager().getHero(corPlayer.getPlayer());
		
		ExperienceType type = HeroClass.ExperienceType.KILLING;		
		if(hero.hasParty()){
			hero.getParty().gainExp(xp, type, victim.getLocation());
		} else if(hero.canGain(type)){
			hero.gainExp(xp, type, victim.getLocation());
		}
	}
	
}

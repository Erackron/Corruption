package com.mcdr.corruption.handler;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.api.ExperienceAPI;
import com.mcdr.corruption.entity.Boss;

public class mcMMOHandler {
	
	public static void addMeleeXP(Player p, Boss boss){
		
		ItemStack weapon = p.getItemInHand();
			
		switch(weapon.getTypeId()){
		case 0:
			ExperienceAPI.addXP(p, "unarmed", boss.getBossData().getMCMMOXPBonus());
			break;
		case 267:
		case 268:
		case 272:
		case 276:
		case 283:
			ExperienceAPI.addXP(p, "swords", boss.getBossData().getMCMMOXPBonus());
			break;
		case 271:
		case 275:
		case 279:
		case 286:
		case 258:
			ExperienceAPI.addXP(p, "axes", boss.getBossData().getMCMMOXPBonus());
			break;
		}
		
	}
	
	public static void addRangeXP(Player p, Boss boss){
		ExperienceAPI.addXP(p, "archery", boss.getBossData().getMCMMOXPBonus());
	}
}

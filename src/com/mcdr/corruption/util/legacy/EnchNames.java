package com.mcdr.corruption.util.legacy;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;

public enum EnchNames {
	PROTECTION_ENVIRONMENTAL(0, "Protection"),
	PROTECTION_FIRE(1, "Fire Protection"),
	PROTECTION_FALL(2, "Feather Falling"),
	PROTECTION_EXPLOSIONS(3, "Blast Protection"),
	PROTECTION_PROJECTILE(4, "Projectile Protection"),
	OXYGEN(5, "Respiration"),
	WATER_WORKER(6, "Aqua Affinity"),
	THORNS(7, "Thorns"),
	DAMAGE_ALL(16, "Sharpness"),
	DAMAGE_UNDEAD(17, "Smite"),
	DAMAGE_ARTHROPODS(18, "Bane of Arthropods"),
	KNOCKBACK(19, "Knockback"),
	FIRE_ASPECT(20, "Fire Aspect"),
	LOOT_BONUS_MOBS(21, "Looting"),
	DIG_SPEED(32, "Efficiency"),
	SILK_TOUCH(33, "Silk Touch"),
	DURABILITY(34, "Unbreaking"),
	LOOT_BONUS_BLOCK(35, "Fortune"),
	ARROW_DAMAGE(48, "Power"),
	ARROW_KNOCKBACK(49, "Punch"),
	ARROW_FIRE(50, "Flame"),
	ARROW_INFINITE(51, "Infinity");
	
	private final int id;
	private final String dispName;
	private static final Map<Integer, EnchNames> BY_ID = new HashMap<Integer, EnchNames>();
	private static final Map<String, EnchNames> BY_NAME = new HashMap<String, EnchNames>();
	private static final Map<String, EnchNames> BY_DISPNAME = new HashMap<String, EnchNames>();
	
	static{
		for(EnchNames ench: EnchNames.values()){
			BY_ID.put(ench.id, ench);
			BY_NAME.put(ench.toString().toLowerCase(), ench);
			BY_DISPNAME.put(ench.dispName.toLowerCase(), ench);
		}
	}
	
	EnchNames(int id, String dispName){
		this.id = id;
		this.dispName = dispName;
	}
	
	public static String getDisplayName(int id){
		return BY_ID.get(id).dispName;
	}
	
	public static String getNameById(int id){
		return BY_ID.get(id).toString();
	}
	
	public static Enchantment getById(int id){
		return Enchantment.getByName(getNameById(id));
	}
	
	public static String getNameByDisplayName(String dispName){
		if(dispName==null) return null;
		return BY_DISPNAME.get(dispName.toLowerCase()).toString();
	}
	
	public static Enchantment getByDispName(String dispName){
		return Enchantment.getByName(getNameByDisplayName(dispName));
	}
	
	public static int getId(Enchantment ench){
		if(ench==null) return -1;
		return BY_NAME.get(ench.getName().toLowerCase()).id;
	}
}
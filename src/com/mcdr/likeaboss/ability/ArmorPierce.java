package com.mcdr.likeaboss.ability;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.mcdr.likeaboss.entity.Boss;


public class ArmorPierce extends Ability {
	private double value = 25;
	
	@Override
	public void Execute(LivingEntity livingEntity, Boss boss) {
		super.Execute(livingEntity, boss);
		EntityDamageEvent entityDamageEvent = livingEntity.getLastDamageCause();
			
		entityDamageEvent.setDamage(getNewDamage(((Player) livingEntity), entityDamageEvent.getDamage(), value));
		useCooldown(boss);
		sendMessage(boss, livingEntity);
	}
	
	public void setValue(double value) {
		if (value > 100.0)
			this.value = 100.0;
		else if (value < 0.0)
			value = 0;
		else
			this.value = value;
	}
	
	
	private static int HelmetDefense(ItemStack helmet) {
		if (helmet != null) {
			switch (helmet.getType()) {
			case DIAMOND_HELMET:
				return 12;
			case IRON_HELMET:
			case CHAINMAIL_HELMET:
			case GOLD_HELMET:
				return 8;
			case LEATHER_HELMET:
				return 4;
			default:
				return 0;
			}
		}
		
		return 0;
	}
	
	private static int ChestplateDefense(ItemStack chestplate) {
		if (chestplate != null) {
			switch (chestplate.getType()) {
			case DIAMOND_CHESTPLATE:
				return 32;
			case IRON_CHESTPLATE:
				return 24;
			case CHAINMAIL_CHESTPLATE:
			case GOLD_CHESTPLATE:
				return 20;
			case LEATHER_CHESTPLATE:
				return 12;
			default:
				return 0;
			}
		}
		
		return 0;
	}
	
	private static int LeggingsDefense(ItemStack leggings) {
		if (leggings != null) {
			switch (leggings.getType()) {
			case DIAMOND_LEGGINGS:
				return 24;
			case IRON_LEGGINGS:
				return 20;
			case CHAINMAIL_LEGGINGS:
				return 16;
			case GOLD_LEGGINGS:
				return 12;
			case LEATHER_LEGGINGS:
				return 8;
			default:
				return 0;
			}
		}
		
		return 0;
	}
	
	private static int BootsDefense(ItemStack boots) {
		if (boots != null) {
			switch (boots.getType()) {
			case DIAMOND_BOOTS:
				return 12;
			case IRON_BOOTS:
				return 8;
			case CHAINMAIL_BOOTS:
			case GOLD_BOOTS:
			case LEATHER_BOOTS:
				return 4;
			default:
				return 0;
			}
		}
		
		return 0;
	}
	
	public static int getNewDamage(Player p, int damage, double value){
		PlayerInventory playerInventory = p.getInventory();
		double absorption = HelmetDefense(playerInventory.getHelmet()) + ChestplateDefense(playerInventory.getChestplate()) + LeggingsDefense(playerInventory.getLeggings()) + BootsDefense(playerInventory.getBoots());
	 	
	    int newDamage =  (int) (damage * (1 - absorption * (1 - value / 100) / 100) / (1 - absorption / 100));
		
		short baseDurabilityLoss = (short) (damage / 4);
		
		if (baseDurabilityLoss < 1)
			baseDurabilityLoss = 1;
		
		short durabilityLossSurplus = (short) (newDamage / 4 - baseDurabilityLoss);
		
		for (ItemStack armor : playerInventory.getArmorContents())
			armor.setDurability((short) (armor.getDurability() - durabilityLossSurplus));
		return newDamage;
	}
}

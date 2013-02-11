package com.mcdr.corruption.drop;

import org.bukkit.Material;

public class Drop {
	private Material material;
	private short data;
	private double chance;
	private int minQuantity;
	private int maxQuantity;
	
	public Drop(Material material, short data, double chance, int minQuantity, int maxQuantity) {
		this.material = material;
		this.data = data;
		this.chance = chance;
		this.minQuantity = minQuantity;
		this.maxQuantity = maxQuantity;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public short getData() {
		return data;
	}
	
	public double getChance() {
		return chance;
	}
	
	public int getMinQuantity() {
		return minQuantity;
	}
	
	public int getMaxQuantity() {
		return maxQuantity;
	}
}

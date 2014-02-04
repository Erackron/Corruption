package com.mcdr.corruption.drop;

import com.mcdr.corruption.entity.CorItem;

public class Drop {
	private CorItem item;
	private double chance;
	private int minQuantity;
	private int maxQuantity;
	
	public Drop(CorItem item, double chance, int minQuantity, int maxQuantity) {
		this.item = item;
		this.chance = chance;
		this.minQuantity = minQuantity;
		this.maxQuantity = maxQuantity;
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

    public CorItem getItem() {
        return item;
    }
}

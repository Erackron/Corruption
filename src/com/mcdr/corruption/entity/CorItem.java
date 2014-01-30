package com.mcdr.corruption.entity;

import com.mcdr.corruption.util.Utility;
import com.mcdr.corruption.util.legacy.EnchNames;
import com.mcdr.corruption.util.legacy.ItemNames;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;

public class CorItem {
    private int id;
    private int data;
    private int durability;

    private int[] enchantmentIds;
    private int[] enchantmentChances;
    private int[] enchantmentLevels;

    private int[] empty = {0};

    public CorItem(){
        id=data=durability=0;
        enchantmentIds=enchantmentChances=enchantmentLevels=empty;
    }

    public CorItem(int id, int data, int durability, int[] enchantmentIds, int[] enchantmentChances, int[] enchantmentLevels){
        this.id = id;
        this.data = data;
        this.durability = durability;
        this.enchantmentIds = enchantmentIds;
        this.enchantmentChances = enchantmentChances;
        this.enchantmentLevels = enchantmentLevels;
    }

    private int[][] getRandomEnchantments(){
        int[][] empty = {{0},{0}};
        if(id <= 0 || enchantmentIds == null){
            return empty;
        }

        ArrayList<Integer[]> entries = new ArrayList<Integer[]>();
        int amount = enchantmentIds.length;

        for(int i = 0; i < amount; i++){
            if(Utility.Random(0, 100)<=enchantmentChances[i]){
                Integer[] temp = {enchantmentIds[i], enchantmentLevels[i]};
                entries.add(temp);
            }
        }

        int size = entries.size();
        if(size>0){
            int[][] output = new int[size][2];
            for(int i = 0; i < size; i++)
                for(int j = 0; j < 2; j++)
                    output[i][j] = entries.get(i)[j];
            return output;
        } else
            return empty;
    }

    @SuppressWarnings("deprecation")
    public ItemStack getItemStack(){
        ItemStack item = new ItemStack(ItemNames.getById(id));
        item.setData(new MaterialData(ItemNames.getById(id), (byte) data));

        if(durability>0) {
            item.setDurability((short) durability);
        }

        int[][] enchantments = getRandomEnchantments();

        if(enchantments[0]!=null){
            for (int[] enchantment : enchantments) {
                if (enchantment[0] > 0 && enchantment[1] > 0)
                    item.addUnsafeEnchantment(EnchNames.getById(enchantment[0]), enchantment[1]);
            }
        }

        return item;
    }
}

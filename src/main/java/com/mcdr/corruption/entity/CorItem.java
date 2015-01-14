package com.mcdr.corruption.entity;

import com.mcdr.corruption.util.MathUtil;
import com.mcdr.corruption.util.legacy.EnchNames;
import com.mcdr.corruption.util.legacy.ItemNames;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class CorItem {
    private int id;
    private int data;
    private int durability;

    private int[] enchantmentIds;
    private int[] enchantmentChances;
    private int[] enchantmentLevels;

    private String name;

    private List<String> lore;

    private boolean empty = false;

    public CorItem() {
        id = data = durability = 0;
        enchantmentIds = enchantmentChances = enchantmentLevels = new int[]{0};
        empty = true;
    }

    public CorItem(int id, int data, int durability, int[] enchantmentIds, int[] enchantmentChances, int[] enchantmentLevels, String name, List<String> lore) {
        this.id = id;
        this.data = data;
        this.durability = durability;
        this.enchantmentIds = enchantmentIds;
        this.enchantmentChances = enchantmentChances;
        this.enchantmentLevels = enchantmentLevels;
        this.name = name;
        this.lore = lore;
    }

    public int getId() {
        return id;
    }

    public int getData() {
        return data;
    }

    public int getDurability() {
        return durability;
    }

    public int[] getEnchantmentIds() {
        return enchantmentIds;
    }

    public int[] getEnchantmentChances() {
        return enchantmentChances;
    }

    public int[] getEnchantmentLevels() {
        return enchantmentLevels;
    }

    public boolean isEmpty() {
        return empty;
    }


    private int[][] getRandomEnchantments() {
        int[][] empty = {{0}, {0}};
        if (id <= 0 || enchantmentIds == null) {
            return empty;
        }

        ArrayList<Integer[]> entries = new ArrayList<Integer[]>();
        int amount = enchantmentIds.length;

        for (int i = 0; i < amount; i++) {
            if (MathUtil.Random(0, 100) <= enchantmentChances[i]) {
                Integer[] temp = {enchantmentIds[i], enchantmentLevels[i]};
                entries.add(temp);
            }
        }

        int size = entries.size();
        if (size > 0) {
            int[][] output = new int[size][2];
            for (int i = 0; i < size; i++)
                for (int j = 0; j < 2; j++)
                    output[i][j] = entries.get(i)[j];
            return output;
        } else
            return empty;
    }

    @SuppressWarnings("deprecation")
    public ItemStack getItemStack() {
        if (empty)
            return null;

        ItemStack item = new ItemStack(ItemNames.getById(id));

        if (id == 0)
            return item;

        item.setData(new MaterialData(ItemNames.getById(id), (byte) data));

        if (durability > 0) {
            item.setDurability((short) durability);
        }

        if (name != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }

        if (lore != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        int[][] enchantments = getRandomEnchantments();

        if (enchantments[0] != null) {
            for (int[] enchantment : enchantments) {
                if (enchantment[0] > 0 && enchantment[1] > 0)
                    item.addUnsafeEnchantment(EnchNames.getById(enchantment[0]), enchantment[1]);
            }
        }

        return item;
    }
}

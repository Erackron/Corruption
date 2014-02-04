package com.mcdr.corruption.entity;

import com.mcdr.corruption.util.Utility;
import com.mcdr.corruption.util.legacy.ItemNames;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EquipmentSet {

    private String name = "empty";

    private Map<CorItem, Integer[]> helmetsData, chestplatesData, leggingsData, bootsData, weaponsData;
    private CorItem emptyItem = new CorItem();

    /**
     * Empty EquipmentSet
     */
    public EquipmentSet() {
        helmetsData = chestplatesData = leggingsData = bootsData = weaponsData = new HashMap<CorItem, Integer[]>();
    }

    public EquipmentSet(Map<CorItem, Integer[]> helmets, Map<CorItem, Integer[]> chestplates, Map<CorItem, Integer[]> leggings, Map<CorItem, Integer[]> boots, Map<CorItem, Integer[]> weapons, String setName) {
        helmetsData = helmets;
        chestplatesData = chestplates;
        leggingsData = leggings;
        bootsData = boots;
        weaponsData = weapons;
        name = setName;
    }

    /**
     * Sets a random EquipmentSet complying to the rules set in the equipment.yml and items.yml files
     *
     * @param le the LivingEntity to set the equipment of
     * @return the processed EntityEquipment object containing the equipment set
     */
    public EntityEquipment setRandomEquipment(LivingEntity le) {
        EntityEquipment eq = le.getEquipment();
        CorItem helmet = getRandomEntry(helmetsData),
                chestplate = getRandomEntry(chestplatesData),
                leggings = getRandomEntry(leggingsData),
                boots = getRandomEntry(bootsData),
                weapon = getRandomEntry(weaponsData);
        ItemStack[] armor = {boots.getItemStack(), leggings.getItemStack(), chestplate.getItemStack(), helmet.getItemStack()};
        ItemStack weaponI = weapon.getItemStack();

        // Set default weapon if none is present
        if (weaponI == null) {
            switch (le.getType()) {
                case PIG_ZOMBIE:
                    weaponI = new ItemStack(ItemNames.getById(283));
                    break;
                case SKELETON:
                    switch (((Skeleton) le).getSkeletonType()) {
                        case NORMAL:
                            weaponI = new ItemStack(ItemNames.getById(261));
                            break;
                        case WITHER:
                            weaponI = new ItemStack(ItemNames.getById(272));
                            break;
                    }
                    break;
                default:
                    break;
            }
        }

        // Apply the weapon/armour to the LivingEntity
        eq.setArmorContents(armor);
        eq.setItemInHand(weaponI);

        // Set the drop chances if applicable
        if (armor[0] != null && bootsData.get(boots)[1] > 0)
            eq.setBootsDropChance((float) (bootsData.get(boots)[1] / 100));
        if (armor[1] != null && leggingsData.get(leggings)[1] > 0)
            eq.setLeggingsDropChance((float) (leggingsData.get(leggings)[1] / 100));
        if (armor[2] != null && chestplatesData.get(chestplate)[1] > 0)
            eq.setChestplateDropChance((float) (chestplatesData.get(chestplate)[1] / 100));
        if (armor[3] != null && helmetsData.get(helmet)[1] > 0)
            eq.setHelmetDropChance((float) (helmetsData.get(helmet)[1] / 100));
        if (weaponI != null && weaponsData.get(weapon)[1] > 0)
            eq.setItemInHandDropChance((float) (weaponsData.get(weapon)[1] / 100));

        //Return the EntityEquipment object for manual changes
        return eq;
    }

    /**
     * Get the added value of all the chance values (the first entry of every Map value) from a Map<CorItem, Integer[]>
     *
     * @param data the Map<CorItem, Integer[]> containing the appropriate data
     * @return the added value of all the chance values
     */
    public int getMaxChance(Map<CorItem, Integer[]> data) {
        int chance = 0;
        for (Integer[] value : data.values()) {
            chance += value[0];
        }
        return chance;
    }

    /**
     * Get the a random CorItem entry from a Map<CorItem, Integer[]> based on the chance values (the first entry of every Integer[] value)
     *
     * @param data the Map<CorItem, Integer[]> containing the appropriate data
     * @return the randomly chosen CorItem
     */
    public CorItem getRandomEntry(Map<CorItem, Integer[]> data) {
        int maxChance = getMaxChance(data);
        if (maxChance <= 0)
            return emptyItem;

        int curChance = 0, random = (maxChance < 100) ? Utility.Random(0, 100) : Utility.Random(0, maxChance);
        for (Map.Entry<CorItem, Integer[]> entry : data.entrySet()) {
            curChance += entry.getValue()[0];
            if (random <= curChance)
                return entry.getKey();
        }

        return emptyItem;
    }

    /**
     * @return the name of the equipmentSet
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the set to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public boolean isEmpty() {
        return name.equalsIgnoreCase("empty");
    }
}
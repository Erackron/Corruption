package com.mcdr.corruption.entity;

import java.util.ArrayList;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.mcdr.corruption.util.Utility;

public class EquipmentSet {

	private String name = "empty";
	private int[][][] helmetsData, chestplatesData, leggingsData, bootsData, weaponsData, empty = {{{0},{0},{0},{0},{0},{0},{0},{0}}};
	
	/**
	 * Empty EquipmentSet
	 */
	public EquipmentSet(){
		helmetsData = chestplatesData = leggingsData = bootsData = weaponsData = empty;
	}
	
	public EquipmentSet(int[][][] helmets, int[][][] chestplates, int[][][] leggings, int[][][] boots, int[][][] weapons, String setName){
		helmetsData = helmets;
		chestplatesData = chestplates;
		leggingsData = leggings;
		bootsData = boots;
		weaponsData = weapons;
		name = setName;
	}
	
	public static int[][][] makeDataArray(int[] itemIds, int[] probabilities, int[] itemData, int[] durability, int[] dropChances, int[][] enchantmentIds, int[][] enchantmentChances, int[][] enchantmentLvls){
		int amount = itemIds.length;
		int[][][] dataArr = new int[amount][8][];
		for(int i = 0; i < amount; i++){
			dataArr[i][0] = new int[]{itemIds[i]};
			dataArr[i][1] = new int[]{probabilities[i]};
			dataArr[i][2] = new int[]{itemData[i]};
			dataArr[i][3] = new int[]{durability[i]};
			dataArr[i][4] = new int[]{dropChances[i]};
			dataArr[i][5] = enchantmentIds[i];
			dataArr[i][6] = enchantmentChances[i];
			dataArr[i][7] = enchantmentLvls[i];
		}
		return dataArr;
	}
	
	/**
	 * Sets a random equipmentset complying to the rules set in the equipment.yml file
	 * @param le the LivingEntity to set the equipment of
	 * @return the processed EntityEquipment object containing the equipment set
	 */
	public EntityEquipment setRandomEquipment(LivingEntity le){
		EntityEquipment eq = le.getEquipment();
		int[][] helmet = getRandomEntry(helmetsData),
				helmetEnch = getRandomEnchantments(helmet),
				chestplate = getRandomEntry(chestplatesData),
				chestplateEnch = getRandomEnchantments(chestplate),
				leggings = getRandomEntry(leggingsData),
				leggingsEnch = getRandomEnchantments(leggings),
				boots = getRandomEntry(bootsData),
				bootsEnch = getRandomEnchantments(boots),
				weapon = getRandomEntry(weaponsData),
				weaponEnch = getRandomEnchantments(weapon);
		ItemStack[] armor = {processEquipment(boots,bootsEnch),processEquipment(leggings, leggingsEnch),processEquipment(chestplate, chestplateEnch),processEquipment(helmet, helmetEnch)};
		ItemStack weaponI = processEquipment(weapon, weaponEnch);
		
		// Set default weapon if none is present
		if(weaponI==null){
			switch(le.getType()){
				case PIG_ZOMBIE:
					weaponI = new ItemStack(283);
					break;
				case SKELETON:
					switch(((Skeleton) le).getSkeletonType()){
					case NORMAL:
						weaponI = new ItemStack(261);
						break;
					case WITHER:
						weaponI = new ItemStack(272);
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
		if(armor[0]!=null && helmet[4][0]>0)
			eq.setHelmetDropChance((float)(helmet[4][0]/100));
		if(armor[1]!=null && chestplate[4][0]>0)
			eq.setChestplateDropChance((float)(chestplate[4][0]/100));
		if(armor[2]!=null && leggings[4][0]>0)
			eq.setLeggingsDropChance((float)(leggings[4][0]/100));
		if(armor[3]!=null && boots[4][0]>0)
			eq.setBootsDropChance((float)(boots[4][0]/100));
		if(weaponI!=null && weapon[4][0]>0)
			eq.setItemInHandDropChance((float)(weapon[4][0]/100));
		
		//Return the EntityEquipment object for manual changes
		return eq;
	}
	
	/**
	 * @param itemData the twodimesional array containing the appropriate data
	 * @param enchantments the twodimensional array containing the enchantments
	 * @return the processed itemstack
	 */
	public ItemStack processEquipment(int[][] itemData, int[][] enchantments){
		if(itemData[0][0]<=0)
			return null;
		
		//Make the new item using its Id
		ItemStack item = new ItemStack(itemData[0][0]);
		//Set the item data
		if(itemData[1][0]>0)
			item.setData(new MaterialData(itemData[0][0],(byte)itemData[1][0]));
		//Set the item durability
		if(itemData[2][0]>0)
			item.setDurability((short)itemData[2][0]);
		
		if(enchantments[0]!=null){
			int enchAmount = enchantments.length;
			for(int i = 0; i < enchAmount; i++){
				if(enchantments[i][0]>0 && enchantments[i][1]>0)
					item.addUnsafeEnchantment(Enchantment.getById(enchantments[i][0]), enchantments[i][1]);
			}
		}
		
		return item;
	}
	
	
	/**
	 * Get the added value of all the chance values (the second entry of every first entry) from a twodimensional array
	 * @param data the twodimensional array containing the appropriate data
	 * @return the added value of all the chance values
	 */
	public int getMaxChance(int[][][] data){
		int chance = 0;
		for(int[][] tempData: data){
			chance += tempData[1][0];
		}
		return chance;
	}
	
	/**
	 * Get the index of a random entry from a twodimensional array based on the chance values (the second entry of every first entry)
	 * @param data the threedimensional array containing the appropriate data
	 * @return the index of the randomly chosen entry
	 */
	public int[][] getRandomEntry(int[][][] data){
		int maxChance = getMaxChance(data);	
		if(maxChance<=0)
			return empty[0];
		
		int	curChance = 0, curEntry = 0, random = (maxChance<100)?Utility.Random(0, 100):Utility.Random(0, maxChance);
		for(int[][] tempData: data){
			curChance += tempData[1][0];
			if(random <= curChance)
				break;
			curEntry++;
		}
		if(random > curChance)
			return empty[0];
		return data[curEntry];
	}
	
	/**
	 * Get an array 
	 * @param data the twodimensional array containing the enchantment data
	 * @return a two dimensional array containing the enchamtments that were chosen based on their probability
	 */
	public int[][] getRandomEnchantments(int[][] data){
		int[][] empty = {{0},{0}};
		if(data[0][0]<=0 || data[5]==null){
			return empty;
		}
		
		ArrayList<Integer[]> entries = new ArrayList<Integer[]>();
		int amount = data[5].length;
		
		for(int i = 0; i < amount; i++){
			if(Utility.Random(0, 100)<=data[6][i]){
				Integer[] temp = {data[5][i], data[7][i]};
				entries.add(temp);
			}
		}
		
		int size = entries.size();
		if(size>0){
			int[][] output = new int[size][2];
			for(int i = 0; i < size; i++)
				for(int j = 0; j < 2; j++)
					output[i][j] = (int)entries.get(i)[j];
			return output;
		} else
			return empty;
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
	
	/**
	 * @return the helmetsData
	 */
	public int[][][] getHelmetsData() {
		return helmetsData;
	}

	/**
	 * @param helmetsData the helmetsData to set
	 */
	public void setHelmetsData(int[][][] helmetsData) {
		this.helmetsData = helmetsData;
	}

	/**
	 * @return the chestplatesData
	 */
	public int[][][] getChestplatesData() {
		return chestplatesData;
	}

	/**
	 * @param chestplatesData the chestplatesData to set
	 */
	public void setChestplatesData(int[][][] chestplatesData) {
		this.chestplatesData = chestplatesData;
	}

	/**
	 * @return the leggingsData
	 */
	public int[][][] getLeggingsData() {
		return leggingsData;
	}

	/**
	 * @param leggingsData the leggingsData to set
	 */
	public void setLeggingsData(int[][][] leggingsData) {
		this.leggingsData = leggingsData;
	}

	/**
	 * @return the bootsData
	 */
	public int[][][] getBootsData() {
		return bootsData;
	}

	/**
	 * @param bootsData the bootsData to set
	 */
	public void setBootsData(int[][][] bootsData) {
		this.bootsData = bootsData;
	}

	/**
	 * @return the weaponsData
	 */
	public int[][][] getWeaponsData() {
		return weaponsData;
	}

	/**
	 * @param weaponsData the weaponsData to set
	 */
	public void setWeaponsData(int[][][] weaponsData) {
		this.weaponsData = weaponsData;
	}
}
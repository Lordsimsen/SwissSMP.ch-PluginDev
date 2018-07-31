package ch.swisssmp.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {
	public static boolean isHelmet(ItemStack itemStack){
		if(itemStack==null)return false;
		Material material = itemStack.getType();
		return 
				material==Material.LEATHER_HELMET||
				material==Material.CHAINMAIL_HELMET||
				material==Material.IRON_HELMET||
				material==Material.GOLD_HELMET||
				material==Material.DIAMOND_HELMET;
	}
	
	public static boolean isChestplate(ItemStack itemStack){
		if(itemStack==null)return false;
		Material material = itemStack.getType();
		return 
				material==Material.LEATHER_CHESTPLATE||
				material==Material.CHAINMAIL_CHESTPLATE||
				material==Material.IRON_CHESTPLATE||
				material==Material.GOLD_CHESTPLATE||
				material==Material.DIAMOND_CHESTPLATE;
	}
	
	public static boolean isLeggings(ItemStack itemStack){
		if(itemStack==null)return false;
		Material material = itemStack.getType();
		return 
				material==Material.LEATHER_LEGGINGS||
				material==Material.CHAINMAIL_LEGGINGS||
				material==Material.IRON_LEGGINGS||
				material==Material.GOLD_LEGGINGS||
				material==Material.DIAMOND_LEGGINGS;
	}
	
	public static boolean isBoots(ItemStack itemStack){
		if(itemStack==null)return false;
		Material material = itemStack.getType();
		return 
				material==Material.LEATHER_BOOTS||
				material==Material.CHAINMAIL_BOOTS||
				material==Material.IRON_BOOTS||
				material==Material.GOLD_BOOTS||
				material==Material.DIAMOND_BOOTS;
	}
}

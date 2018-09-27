package ch.swisssmp.utils;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_13_R2.NBTTagCompound;

public class ItemUtil {
	public static boolean isHelmet(ItemStack itemStack){
		if(itemStack==null)return false;
		Material material = itemStack.getType();
		return 
				material==Material.LEATHER_HELMET||
				material==Material.CHAINMAIL_HELMET||
				material==Material.IRON_HELMET||
				material==Material.GOLDEN_HELMET||
				material==Material.DIAMOND_HELMET;
	}
	
	public static boolean isChestplate(ItemStack itemStack){
		if(itemStack==null)return false;
		Material material = itemStack.getType();
		return 
				material==Material.LEATHER_CHESTPLATE||
				material==Material.CHAINMAIL_CHESTPLATE||
				material==Material.IRON_CHESTPLATE||
				material==Material.GOLDEN_CHESTPLATE||
				material==Material.DIAMOND_CHESTPLATE;
	}
	
	public static boolean isLeggings(ItemStack itemStack){
		if(itemStack==null)return false;
		Material material = itemStack.getType();
		return 
				material==Material.LEATHER_LEGGINGS||
				material==Material.CHAINMAIL_LEGGINGS||
				material==Material.IRON_LEGGINGS||
				material==Material.GOLDEN_LEGGINGS||
				material==Material.DIAMOND_LEGGINGS;
	}
	
	public static boolean isBoots(ItemStack itemStack){
		if(itemStack==null)return false;
		Material material = itemStack.getType();
		return 
				material==Material.LEATHER_BOOTS||
				material==Material.CHAINMAIL_BOOTS||
				material==Material.IRON_BOOTS||
				material==Material.GOLDEN_BOOTS||
				material==Material.DIAMOND_BOOTS;
	}
	
	public static Position getPosition(ItemStack itemStack, String key){
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		if(!nmsStack.hasTag()) return null;
		return NBTTagUtil.getPosition(nmsStack.getTag(), key);
	}
	
	public static void setPosition(ItemStack itemStack, String key, Position position){
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound nbtTag = nmsStack.getTag();
		NBTTagUtil.setPosition(nbtTag, key, position);
		nmsStack.setTag(nbtTag);
		itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));
	}
	
	public static String getString(ItemStack itemStack, String key){
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		if(!nmsStack.hasTag()) return null;
		NBTTagCompound nbtTag = nmsStack.getTag();
		if(!nbtTag.hasKey(key)) return null;
		return nbtTag.getString(key);
	}
	
	public static void setString(ItemStack itemStack, String key, String value){
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound nbtTag = nmsStack.getTag();
		nbtTag.setString(key, value);
		nmsStack.setTag(nbtTag);
		itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));
	}
	
	public static int getInt(ItemStack itemStack, String key){
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		if(!nmsStack.hasTag()) return 0;
		NBTTagCompound nbtTag = nmsStack.getTag();
		if(!nbtTag.hasKey(key)) return 0;
		return nbtTag.getInt(key);
	}
	
	public static void setInt(ItemStack itemStack, String key, int value){
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound nbtTag = nmsStack.getTag();
		nbtTag.setInt(key, value);
		nmsStack.setTag(nbtTag);
		itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));
	}
	
	public static double getDouble(ItemStack itemStack, String key){
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		if(!nmsStack.hasTag()) return 0;
		NBTTagCompound nbtTag = nmsStack.getTag();
		if(!nbtTag.hasKey(key)) return 0;
		return nbtTag.getDouble(key);
	}
	
	public static void setDouble(ItemStack itemStack, String key, double value){
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound nbtTag = nmsStack.getTag();
		nbtTag.setDouble(key, value);
		nmsStack.setTag(nbtTag);
		itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));
	}
	
	public static boolean getBoolean(ItemStack itemStack, String key){
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		if(!nmsStack.hasTag()) return false;
		NBTTagCompound nbtTag = nmsStack.getTag();
		if(!nbtTag.hasKey(key)) return false;
		return nbtTag.getBoolean(key);
	}
	
	public static void setBoolean(ItemStack itemStack, String key, boolean value){
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound nbtTag = nmsStack.getTag();
		nbtTag.setBoolean(key, value);
		nmsStack.setTag(nbtTag);
		itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));
	}
}

package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.nbt.NBTTagCompound;

import java.util.function.Consumer;
import java.util.function.Function;

public class ItemUtil {
	
	public static String serialize(ItemStack itemStack) {
		org.bukkit.configuration.file.YamlConfiguration yamlConfiguration = new YamlConfiguration();
		yamlConfiguration.set("item", itemStack);
		return Base64.encodeBase64String(yamlConfiguration.saveToString().getBytes());
	}
	
	public static ItemStack deserialize(String s) {
		if(Base64.isBase64(s)){
			org.bukkit.configuration.file.YamlConfiguration yamlConfiguration = new YamlConfiguration();
			try {
				yamlConfiguration.loadFromString(new String(Base64.decodeBase64(s)));
				return yamlConfiguration.getItemStack("item");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
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
		net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		if(!nmsStack.hasTag()) return null;
		NBTTagCompound nbtTag = new NBTTagCompound(nmsStack.getTag());
		return nbtTag.getPosition(key);
	}
	
	public static void setPosition(ItemStack itemStack, String key, Position position){
		net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound nbtTag = nmsStack.hasTag() ? new NBTTagCompound(nmsStack.getTag()) : new NBTTagCompound();
		nbtTag.setPosition(key, position);
		nmsStack.setTag(nbtTag.asNMS());
		itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));
	}
	
	public static String getString(ItemStack itemStack, String key){
		net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		if(!nmsStack.hasTag()) return null;
		NBTTagCompound nbtTag = new NBTTagCompound(nmsStack.getTag());
		return nbtTag.getString(key,null);
	}
	
	public static void setString(ItemStack itemStack, String key, String value){
		net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound nbtTag = nmsStack.hasTag() ? new NBTTagCompound(nmsStack.getTag()) : new NBTTagCompound();
		nbtTag.setString(key, value);
		nmsStack.setTag(nbtTag.asNMS());
		itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));
	}
	
	public static int getInt(ItemStack itemStack, String key){
		return ItemUtil.getInt(itemStack, key, 0);
	}
	
	public static int getInt(ItemStack itemStack, String key, int fallback){
		net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		if(!nmsStack.hasTag()) return fallback;
		NBTTagCompound nbtTag = new NBTTagCompound(nmsStack.getTag());
		return nbtTag.getInt(key,fallback);
	}
	
	public static void setInt(ItemStack itemStack, String key, int value){
		net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound nbtTag = nmsStack.hasTag() ? new NBTTagCompound(nmsStack.getTag()) : new NBTTagCompound();
		nbtTag.setInt(key, value);
		nmsStack.setTag(nbtTag.asNMS());
		itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));
	}
	
	public static double getDouble(ItemStack itemStack, String key){
		return ItemUtil.getDouble(itemStack, key, 0);
	}
	
	public static double getDouble(ItemStack itemStack, String key, double fallback){
		net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		if(!nmsStack.hasTag()) return fallback;
		NBTTagCompound nbtTag = new NBTTagCompound(nmsStack.getTag());
		return nbtTag.getDouble(key,fallback);
	}
	
	public static void setDouble(ItemStack itemStack, String key, double value){
		net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound nbtTag = nmsStack.hasTag() ? new NBTTagCompound(nmsStack.getTag()) : new NBTTagCompound();
		nbtTag.setDouble(key, value);
		nmsStack.setTag(nbtTag.asNMS());
		itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));
	}
	
	public static boolean getBoolean(ItemStack itemStack, String key){
		return ItemUtil.getBoolean(itemStack, key, false);
	}
	
	public static boolean getBoolean(ItemStack itemStack, String key, boolean fallback){
		net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		if(!nmsStack.hasTag()) return fallback;
		NBTTagCompound nbtTag = new NBTTagCompound(nmsStack.getTag());
		return nbtTag.getBoolean(key, fallback);
	}
	
	public static void setBoolean(ItemStack itemStack, String key, boolean value){
		net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound nbtTag = nmsStack.hasTag() ? new NBTTagCompound(nmsStack.getTag()) : new NBTTagCompound();
		nbtTag.setBoolean(key, value);
		nmsStack.setTag(nbtTag.asNMS());
		itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));
	}
	
	public static void setData(ItemStack itemStack, NBTTagCompound nbtTag){
		net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		nmsStack.setTag(nbtTag.asNMS());
		itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));
	}
	
	public static NBTTagCompound getData(ItemStack itemStack){
		net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		if(!nmsStack.hasTag()) return null;
		return new NBTTagCompound(nmsStack.getTag());
	}

	public static void updateItemsGlobal(Function<ItemStack,Boolean> filter, Consumer<ItemStack> callback){
		for(Player player : Bukkit.getOnlinePlayers()){
			updateItems(player.getInventory(), filter, callback);
			if(player.getOpenInventory()!=null){
				updateItems(player.getOpenInventory().getTopInventory(), filter, callback);
			}
		}
	}

	public static void updateItems(Inventory inventory, Function<ItemStack,Boolean> filter, Consumer<ItemStack> callback){
		for(ItemStack itemStack : inventory){
			if(itemStack==null || itemStack.getType()==Material.AIR) continue;
			if(!filter.apply(itemStack)) continue;
			callback.accept(itemStack);
		}
	}
}

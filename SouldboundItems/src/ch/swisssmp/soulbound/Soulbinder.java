package ch.swisssmp.soulbound;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.utils.ItemUtil;

public class Soulbinder {

	private static final String SoulboundKey = "Soulbound";
	private static final String SoulboundOwnerKey = "SoulboundOwner";

	private static final String SoulboundLore = ChatColor.LIGHT_PURPLE+"Seelengebunden";
	
	public static boolean bind(ItemStack itemStack, UUID playerUid, String displayName) {
		return bind(itemStack, playerUid, displayName, false);
	}
	public static boolean bind(ItemStack itemStack, UUID playerUid, String displayName, boolean allowOverwrite) {
		UUID previous = getOwner(itemStack);
		boolean wasSoulbound = previous!=null;
		if(!allowOverwrite && wasSoulbound) {
			return false;
		}
		
		ItemUtil.setBoolean(itemStack, SoulboundKey, true);
		ItemUtil.setString(itemStack, SoulboundOwnerKey, playerUid.toString());
		
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<String>();
		if(wasSoulbound) {
			int soulboundOwnerLine = lore.indexOf(SoulboundLore)+1;
			if(soulboundOwnerLine>0 && soulboundOwnerLine < lore.size()) {
				lore.set(soulboundOwnerLine, displayName);
				return true;
			}
		}
		if(!lore.contains(SoulboundLore)) {
			if(lore.size()>0) lore.add("");
			lore.add(SoulboundLore);
		}
		lore.add(displayName);
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		return true;
	}
	public static void unbind(ItemStack itemStack, boolean keepSoulboundAttribute) {
		ItemUtil.setBoolean(itemStack, SoulboundKey, keepSoulboundAttribute);
		ItemUtil.setString(itemStack, SoulboundOwnerKey, null);

		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<String>();
		int soulboundOwnerHeaderLine = lore.indexOf(SoulboundLore);
		if(soulboundOwnerHeaderLine>=0 && soulboundOwnerHeaderLine < lore.size()) {
			lore.remove(soulboundOwnerHeaderLine);
			if(soulboundOwnerHeaderLine<lore.size()) {
				lore.remove(soulboundOwnerHeaderLine);
			}
		}
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
	}
	/**
	 * Marks a given item with the Soulbound attribute
	 * @param itemStack
	 */
	public static void setSoulbound(ItemStack itemStack) {
		if(ItemUtil.getBoolean(itemStack, SoulboundKey)) return;
		ItemUtil.setBoolean(itemStack, SoulboundKey, true);
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(!itemMeta.hasEnchants()) {
			itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		List<String> lore = itemMeta.getLore();
		if(lore.size()>0) lore.add("");
		lore.add(SoulboundLore);
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
	}
	/**
	 * Returns whether a given item has the Soulbound attribute
	 * @param itemStack
	 * @return <code>true</code> if the item is soulbound; otherwise <code>false</code>
	 */
	public static boolean isSoulbound(ItemStack itemStack) {
		return ItemUtil.getBoolean(itemStack, SoulboundKey);
	}
	public static UUID getOwner(ItemStack itemStack) {
		String ownerString = ItemUtil.getString(itemStack, SoulboundOwnerKey);
		try {
			return UUID.fromString(ownerString);
		}
		catch(Exception e){
			return null;
		}
	}
}

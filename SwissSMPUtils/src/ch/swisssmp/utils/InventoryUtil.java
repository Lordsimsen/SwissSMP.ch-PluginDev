package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtil {
	/**
	 * Clones all ItemStacks from one Inventory into another
	 * @param template
	 * @param target
	 */
	public static void clone(Inventory template, Inventory target){
		for(int i = 0; i < template.getSize(); i++){
			target.setItem(i, (template.getItem(i)!=null ? template.getItem(i).clone() : null));
		}
		if(template instanceof PlayerInventory) InventoryUtil.clonePlayerInventory((PlayerInventory)template, (PlayerInventory)target);
		
	}
	
	/**
	 * Waits one server tick to then set the slot to the defined ItemStack
	 * @param inventory - The inventory to modify
	 * @param slot - The slot to modify
	 * @param itemStack - The ItemStack to put into the given slot
	 */
	public static void refillInventorySlot(Inventory inventory, int slot, ItemStack itemStack){
		if(itemStack==null) return;
		Bukkit.getScheduler().runTaskLater(SwissSMPUtils.plugin, new Runnable(){
			public void run(){
				inventory.setItem(slot, itemStack);
			}
		}, 1L);
	}
	
	private static void clonePlayerInventory(PlayerInventory template, PlayerInventory target){
		if(template.getItemInOffHand()==null)return;
		target.setItemInOffHand(template.getItemInOffHand().clone());
	}
}

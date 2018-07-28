package ch.swisssmp.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtil {
	public static void clone(Inventory template, Inventory target){
		for(int i = 0; i < template.getSize(); i++){
			target.setItem(i, (template.getItem(i)!=null ? template.getItem(i).clone() : null));
		}
		if(template instanceof PlayerInventory) InventoryUtil.clonePlayerInventory((PlayerInventory)template, (PlayerInventory)target);
		
	}
	private static void clonePlayerInventory(PlayerInventory template, PlayerInventory target){
		if(template.getItemInOffHand()==null)return;
		target.setItemInOffHand(template.getItemInOffHand().clone());
	}
}

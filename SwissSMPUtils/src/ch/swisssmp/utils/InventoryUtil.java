package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
		Bukkit.getScheduler().runTaskLater(SwissSMPUtils.plugin, () -> inventory.setItem(slot, itemStack), 1L);
	}
	
	public static JsonObject serialize(Inventory inventory) {
		JsonObject result = new JsonObject();
		JsonArray slots = new JsonArray();
		for(int i = 0; i < inventory.getSize(); i++) {
			ItemStack itemStack = inventory.getItem(i);
			if(itemStack==null) continue;
			JsonObject itemSection = new JsonObject();
			itemSection.addProperty("s", i);
			itemSection.addProperty("i", ItemUtil.serialize(itemStack));
			slots.add(itemSection);
		}
		result.add("slots", slots);
		return result;
	}
	
	public static void deserialize(JsonObject data, Inventory inventory) {
		if(!data.has("slots")) return;
		JsonArray slots = data.get("slots").getAsJsonArray();
		for(JsonElement element : slots) {
			if(!element.isJsonObject()) continue;
			JsonObject slotSection = element.getAsJsonObject();
			int slot = slotSection.get("s").getAsInt();
			ItemStack itemStack = ItemUtil.deserialize(slotSection.get("i").getAsString());
			inventory.setItem(slot, itemStack);
		}
	}
	
	private static void clonePlayerInventory(PlayerInventory template, PlayerInventory target){
		if(template.getItemInOffHand()==null)return;
		target.setItemInOffHand(template.getItemInOffHand().clone());
	}
}

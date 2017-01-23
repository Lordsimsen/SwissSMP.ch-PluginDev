package ch.swisssmp.craftmmo.mmoplayer;

import java.util.Map.Entry;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.craftmmo.mmoattribute.IDurable;
import ch.swisssmp.craftmmo.mmoitem.MmoItem;

public class MmoInventoryListener implements Listener{
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onItemPickup(PlayerPickupItemEvent event){
		Item item = event.getItem();
		ItemStack itemStack = item.getItemStack();
		MmoItem mmoItem = MmoItem.get(itemStack);
		if(mmoItem==null || itemStack.getAmount()<=1){
			return;
		}
		event.setCancelled(true);
		MmoItem.update(itemStack);
		item.setItemStack(itemStack);
		Player player = event.getPlayer();
		PlayerInventory playerInventory = player.getInventory();
		for(Entry<Integer, ? extends ItemStack> entry : playerInventory.all(itemStack.getType()).entrySet()){
			ItemStack inventoryStack = entry.getValue();
			MmoItem mmoInventoryItem = MmoItem.get(inventoryStack);
			if(mmoInventoryItem==mmoItem){
				int sum = inventoryStack.getAmount()+itemStack.getAmount();
				int newStackSize = Math.min(mmoItem.maxStackSize, sum);
				int leftover = sum-newStackSize;
				if(leftover>0){
					itemStack.setAmount(leftover);
					inventoryStack.setAmount(newStackSize);
				}
				else{
					itemStack.setAmount(newStackSize);
					playerInventory.setItem(entry.getKey(), itemStack);
					item.remove();
					return;
				}
			}
		}
	}
	
	@EventHandler
	private void onInventoryChange(InventoryClickEvent event){
		ItemStack cursor = event.getCursor();
		ItemStack slot = event.getCurrentItem();
		MmoItem mmoCursorItem = MmoItem.get(cursor);
		MmoItem mmoSlotItem = MmoItem.get(slot);
		if(mmoCursorItem==mmoSlotItem && mmoCursorItem!=null && !(mmoCursorItem instanceof IDurable)){
			int sum = slot.getAmount()+cursor.getAmount();
			int newStackSize = Math.min(mmoSlotItem.maxStackSize, sum);
			int leftover = sum-newStackSize;
			cursor.setAmount(newStackSize);
			if(leftover>0){
				slot.setAmount(leftover);
			}
			else{
				event.getClickedInventory().setItem(event.getSlot(), null);
			}
			event.setResult(Result.ALLOW);
		}
	}
}

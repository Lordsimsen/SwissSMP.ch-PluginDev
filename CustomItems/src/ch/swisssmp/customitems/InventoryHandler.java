package ch.swisssmp.customitems;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.ItemUtil;

public class InventoryHandler {
	protected static void handleInventoryClick(InventoryClickEvent event){
		//System.out.println("InventoryClickEvent ("+event.getClick().toString()+")");
		ItemStack slotStack = (event.getClickedInventory()!=null) ? event.getClickedInventory().getItem(event.getSlot()) : null;
		if(event.getClick()!=ClickType.DOUBLE_CLICK && (slotStack==null || slotStack.getType()==Material.AIR)) return; //all click types except DOUBLE_CLICK require slot to contain stack
		int slotMaxStackSize = (slotStack!=null && slotStack.getType()!=Material.AIR) ? ItemUtil.getInt(slotStack, "maxStackSize") : 0;
		if(event.getClick()!=ClickType.DOUBLE_CLICK && slotMaxStackSize==0) return;
		ItemStack cursorStack = event.getCursor();
		boolean cursorIsEmpty = (cursorStack==null || cursorStack.getType()==Material.AIR);
		int cursorMaxStackSize = (!cursorIsEmpty) ? ItemUtil.getInt(cursorStack, "maxStackSize") : 0;
		switch(event.getClick()){
		case DOUBLE_CLICK:{ //requires slot to contain stack and cursor to be empty
			if(cursorIsEmpty || cursorMaxStackSize==0) return;
			InventoryHandler.handleInventoryDoubleClick(event.getView(), event.getClickedInventory(), cursorStack, event.getSlot(), cursorMaxStackSize);
			event.setCancelled(true);
			return;
		}
		case MIDDLE:{ //requires slot to contain stack and cursor to be empty
			if(!cursorIsEmpty || event.getWhoClicked().getGameMode()!=GameMode.CREATIVE) return; //exclusive to creative mode
			InventoryHandler.handleInventoryMiddleClick(event.getView(), slotStack, slotMaxStackSize);
			event.setCancelled(true);
			return;
		}
		case SHIFT_RIGHT: //same as SHIFT_LEFT
		case SHIFT_LEFT:{ //requires slot to contain stack
			/*
			if(event.getView().getType()==InventoryType.PLAYER){
				InventoryHandler.handlePlayerInventoryShiftClick(event.getView(), slotStack, slotMaxStackSize, event.getClickedInventory(), event.getSlot());
			}
			else{
				InventoryHandler.handleInventoryShiftClick(event.getView(), slotStack, slotMaxStackSize, event.getClickedInventory(), event.getSlot());
			}
			*/
			event.setCancelled(true);
			return;
		}
		case LEFT:{ //requires slot and cursor to contain the same stack
			if(cursorIsEmpty || !cursorStack.isSimilar(slotStack)) return; //not the same item type, items will get swapped instead
			InventoryHandler.handleInventoryLeftClick(event.getView(), slotStack, cursorStack, event.getRawSlot(), slotMaxStackSize);
			event.setCancelled(true);
			return;
		}
		case RIGHT:{ //requires slot and cursor to contain the same stack
			if(cursorIsEmpty || !cursorStack.isSimilar(slotStack)) return; //not the same item type, works as intended
			InventoryHandler.handleInventoryRightClick(event.getView(), slotStack, cursorStack, slotMaxStackSize);
			event.setCancelled(true);
			return;
		}
		case WINDOW_BORDER_LEFT: //simply drops the cursor stack, no stacking required
		case WINDOW_BORDER_RIGHT: //simply drops the cursor stack, no stacking required
		case DROP: //simply drops one of stack, no stacking required
		case CONTROL_DROP: //simply drops the slot stack, no stacking required
		case NUMBER_KEY: //simply swaps slot with hotbar index pressed, no stacking required
		default:{
			return; //unknown click type
		}
		}
	}
	
	protected static void handleInventoryDrag(InventoryDragEvent event){
		//System.out.println("InventoryDragEvent ("+event.getType().toString()+")");
		if(event.getCursor()==null || event.getCursor().getType()==Material.AIR) return;
		ItemStack itemStack = event.getCursor();
		int maxStackSize = ItemUtil.getInt(itemStack, "maxStackSize");
		if(maxStackSize==0) return; //not a stackable custom item
		event.setCancelled(true);
		/*
		switch(event.getType()){
		case EVEN:{
			System.out.println("InventoryDragEven");
			Bukkit.getScheduler().runTaskLater(CustomItems.plugin, ()->{
				InventoryHandler.handleInventoryDragEven(event, maxStackSize);
			}, 1L);
			return;
		}
		case SINGLE:{
			System.out.println("InventoryDragSingle");
			Bukkit.getScheduler().runTaskLater(CustomItems.plugin, ()->{
				InventoryHandler.handleInventoryDragSingle(event, maxStackSize);
			}, 1L);
			return;
		}
		default:{
			return; //unknown drag type
		}
		}
		*/
	}
	
	/**
	 * Sucks all items of the same type to the cursor
	 */
	private static void handleInventoryDoubleClick(InventoryView view, Inventory clickedInventory, ItemStack slotStack, int slot, int maxStackSize){
		ItemStack cursorStack = slotStack.clone();
		ItemStack itemStack;
		int transferredAmount;
		for(int i = 0; i < view.getTopInventory().getSize(); i++){
			if(view.getTopInventory()==clickedInventory && i==slot) continue;
			itemStack = view.getTopInventory().getItem(i);
			if(itemStack==null || itemStack.getType()==Material.AIR || !itemStack.isSimilar(cursorStack)) continue;
			transferredAmount = Math.min(maxStackSize-cursorStack.getAmount(),itemStack.getAmount());
			itemStack.setAmount(itemStack.getAmount()-transferredAmount);
			cursorStack.setAmount(cursorStack.getAmount()+transferredAmount);
			if(cursorStack.getAmount()>=maxStackSize) break;
		}
		if(cursorStack.getAmount()<maxStackSize){
			for(int i = 0; i < view.getBottomInventory().getSize(); i++){
				if(view.getBottomInventory()==clickedInventory && i==slot) continue;
				itemStack = view.getBottomInventory().getItem(i);
				if(itemStack==null || itemStack.getType()==Material.AIR || !itemStack.isSimilar(cursorStack)) continue;
				transferredAmount = Math.min(maxStackSize-cursorStack.getAmount(),itemStack.getAmount());
				itemStack.setAmount(itemStack.getAmount()-transferredAmount);
				cursorStack.setAmount(cursorStack.getAmount()+transferredAmount);
				if(cursorStack.getAmount()>=maxStackSize) break;
			}
		}
		view.setCursor(cursorStack);
		slotStack.setAmount(0);
	}
	
	/**
	 * Clones slot stack to cursor with max stack size
	 */
	private static void handleInventoryMiddleClick(InventoryView view, ItemStack slotStack, int maxStackSize){
		ItemStack itemStack = slotStack.clone();
		itemStack.setAmount(maxStackSize);
		view.setCursor(itemStack);
	}
	
	/**
	 * Stacks cursor on slot if possible
	 */
	private static void handleInventoryLeftClick(InventoryView view, ItemStack slotStack, ItemStack cursorStack, int slot, int maxStackSize){
		int transferredAmount = Math.min(maxStackSize-slotStack.getAmount(), cursorStack.getAmount());
		ItemStack newSlotStack = slotStack.clone();
		ItemStack newCursorStack = cursorStack.clone();
		newSlotStack.setAmount(slotStack.getAmount()+transferredAmount);
		newCursorStack.setAmount(cursorStack.getAmount()-transferredAmount);
		Bukkit.getScheduler().runTaskLater(CustomItemsPlugin.getInstance(), ()->{
			view.setItem(slot, newSlotStack);
			view.setCursor(newCursorStack);
		}, 1L);
	}
	
	/**
	 * Adds one to slot
	 */
	private static void handleInventoryRightClick(InventoryView view, ItemStack slotStack, ItemStack cursorStack, int maxStackSize){
		if(slotStack.getAmount()>=maxStackSize) return;
		slotStack.setAmount(slotStack.getAmount()+1);
		int newCursorStackAmount = cursorStack.getAmount()-1;
		ItemStack newCursorStack = cursorStack.clone();
		newCursorStack.setAmount(newCursorStackAmount);
		Bukkit.getScheduler().runTaskLater(CustomItemsPlugin.getInstance(), ()->{
			view.setCursor(newCursorStack);
		}, 1L);
	}
	
	/**
	 * Spreads cursor evenly
	 */
	/*
	private static void handleInventoryDragEven(InventoryDragEvent event, int maxStackSize){
		int slotCount = event.getRawSlots().size();
		int itemsPerSlot = Mathf.floorToInt(event.getCursor().getAmount()/(float)slotCount);
		ItemStack cursorStack = event.getCursor();
		ItemStack slotStack;
		int transferredAmount;
		for(int slot : event.getRawSlots()){
			slotStack = event.getView().getItem(slot);
			if(slotStack==null || slotStack.getType()==Material.AIR){
				slotStack = cursorStack.clone();
				slotStack.setAmount(itemsPerSlot);
				transferredAmount = itemsPerSlot;
			}
			else{
				transferredAmount = Math.min(itemsPerSlot, maxStackSize-slotStack.getAmount());
			}
			slotStack.setAmount(slotStack.getAmount()+transferredAmount);
			event.getView().setItem(slot, slotStack);
			cursorStack.setAmount(cursorStack.getAmount()-transferredAmount);
		}
		event.getView().setCursor(cursorStack);
	}
	*/
	
	/**
	 * Adds one to each slot
	 */
	/*
	private static void handleInventoryDragSingle(InventoryDragEvent event, int maxStackSize){
		int itemsPerSlot = 1;
		ItemStack cursorStack = event.getCursor();
		ItemStack slotStack;
		int transferredAmount;
		for(int slot : event.getRawSlots()){
			slotStack = event.getView().getItem(slot);
			if(slotStack==null || slotStack.getType()==Material.AIR){
				slotStack = cursorStack.clone();
				slotStack.setAmount(itemsPerSlot);
				transferredAmount = itemsPerSlot;
			}
			else{
				transferredAmount = Math.min(itemsPerSlot, maxStackSize-slotStack.getAmount());
			}
			slotStack.setAmount(slotStack.getAmount()+transferredAmount);
			event.getView().setItem(slot, slotStack);
			cursorStack.setAmount(cursorStack.getAmount()-transferredAmount);
		}
		event.getView().setCursor(cursorStack);
	}
	*/
}

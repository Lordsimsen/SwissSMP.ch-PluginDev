package ch.swisssmp.customitems;

import java.util.Map;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import ch.swisssmp.utils.ItemUtil;

public class EventListener implements Listener{
	@EventHandler(priority=EventPriority.LOWEST)
	private void onPrepareItemCraft(PrepareItemCraftEvent event){
		ItemStack itemStack = event.getInventory().getResult();
		if(itemStack==null) return;
		
		// Bukkit.getLogger().info("CustomItems checking ingredients");
		
		Recipe recipe = event.getRecipe();
		boolean allow;
		if(recipe instanceof ShapedRecipe){
			ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
			String[] shape = shapedRecipe.getShape();
			String[] mirroredShape = getMirroredShape(shape);
			Map<Character,RecipeChoice> ingredients = shapedRecipe.getChoiceMap();
			allow = CustomItems.checkIngredients(shape, ingredients, event.getInventory());
			if(!allow) allow = CustomItems.checkIngredients(mirroredShape, ingredients, event.getInventory());
		}
		else if(recipe instanceof ShapelessRecipe){
			allow = CustomItems.checkIngredients((ShapelessRecipe)recipe, event.getInventory());
		}
		else{
			return;
		}
		// Bukkit.getLogger().info(allow ? "CustomItems allows this recipe" : "CustomItems denies this recipe");
		if(!allow) event.getInventory().setResult(null);
	}
	
	@EventHandler
	private void onInventoryOpen(InventoryOpenEvent event){
		CustomItems.clearExpiredItems(event.getInventory());
	}
	
	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event){
		CustomItems.clearExpiredItems(event.getPlayer().getInventory());
	}
	
	/**
	 * Make custom items stackable when picking them up
	 */
	@EventHandler(ignoreCancelled=true,priority=EventPriority.LOWEST)
	private void onItemPickup(EntityPickupItemEvent event){
		if(!(event.getEntity() instanceof Player)) return;
		ItemStack itemStack = event.getItem().getItemStack();
		String customEnum = CustomItems.getCustomEnum(itemStack);
		if(customEnum==null) return;
		int maxStackSize = ItemUtil.getInt(itemStack, "maxStackSize");
		if(maxStackSize==0) return;
		event.setCancelled(true);
		Player player = (Player)event.getEntity();
		player.playSound(event.getItem().getLocation(), Sound.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, 1.2f);
		PlayerInventory playerInventory = player.getInventory();
		ItemStack inventoryStack;
		int fittingAmount;
		for(int i = 0; i < playerInventory.getSize(); i++){
			inventoryStack = playerInventory.getItem(i);
			if(inventoryStack==null) continue;
			if(!inventoryStack.isSimilar(itemStack)) continue;
			if(inventoryStack.getAmount()>=maxStackSize) continue;
			fittingAmount = Math.min(maxStackSize-inventoryStack.getAmount(),itemStack.getAmount());
			inventoryStack.setAmount(inventoryStack.getAmount()+fittingAmount);
			itemStack.setAmount(itemStack.getAmount()-fittingAmount);
			if(itemStack.getAmount()<=0) break;
		}
		if(itemStack.getAmount()<=0){
			event.getItem().remove();
			return;
		}
		int slot = playerInventory.firstEmpty();
		if(slot<0) return;
		playerInventory.setItem(slot, itemStack);
		event.getItem().remove();
	}
	
	/**
	 * Make custom items stackable when clicking in inventory
	 */
	@EventHandler
	private void onInventoryClick(InventoryClickEvent event){
		InventoryHandler.handleInventoryClick(event);
	}

	/**
	 * Make custom items stackable when dragging in inventory
	 */
	@EventHandler
	private void onInventoryDrag(InventoryDragEvent event){
		InventoryHandler.handleInventoryDrag(event);
	}
	
	private String[] getMirroredShape(String[] shape) {
		String[] result = new String[shape.length];
		for(int i = 0; i < shape.length; i++) {
			String line = shape[i];
			char[] reversed = new char[line.length()];
			for(int j = 0; j < reversed.length; j++) {
				reversed[j] = line.charAt(reversed.length-1-j);
			}
			result[i] = String.valueOf(reversed);
		}
		
		return result;
	}
}

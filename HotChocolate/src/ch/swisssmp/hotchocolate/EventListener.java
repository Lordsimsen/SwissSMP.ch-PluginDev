package ch.swisssmp.hotchocolate;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.SwissSMPler;

public class EventListener implements Listener {
	
	@EventHandler
	private void onResourcepackUpdate(PlayerResourcePackUpdateEvent event) {
		event.addComponent("hotchocolate");
	}
	
	@EventHandler
	private void onPrepareItemCraft(PrepareItemCraftEvent event) {
		Recipe recipe = event.getRecipe();
		if(recipe==null) return;
		ItemStack result = recipe.getResult();
		if(result==null) return;
		CustomMaterial material = CustomMaterial.of(result);
		if(material!=CustomMaterial.HOT_CHOCOLATE && material!=CustomMaterial.CHOCOLATE_POWDER) return;
		
		switch(material) {
		case HOT_CHOCOLATE: 
			validateHotChocolateRecipe(event);
			break;
		default:
			break;
		}
		
	}
	
	private void validateHotChocolateRecipe(PrepareItemCraftEvent event) {
		Recipe recipe = event.getRecipe();
		CraftingInventory inventory = event.getInventory();
		
		if(event.getView().getPlayer()==null || !event.getView().getPlayer().hasPermission("hotchocolate.craft")) {
			inventory.setResult(null);
			return;
		}
		
		ItemStack[] matrix = inventory.getMatrix();
		ItemStack cocoaStack = null;
		for(int i = 0; i < matrix.length; i++) {
			ItemStack ingredient = matrix[i];
			if(ingredient==null) continue;
			if(ingredient.getType()!=Material.COCOA_BEANS) continue;
			if(CustomMaterial.of(ingredient)!=CustomMaterial.CHOCOLATE_POWDER) continue;
			cocoaStack = ingredient;
		}
		
		if(cocoaStack==null) {
			inventory.setResult(null);
			return;
		}
		
		inventory.setResult(recipe.getResult());
	}
	
	@EventHandler
	private void onFoodConsume(PlayerItemConsumeEvent event) {
		if(event.getItem()==null) return;
		ItemStack itemStack = event.getItem();
		CustomMaterial material = CustomMaterial.of(itemStack);
		if(material!=CustomMaterial.HOT_CHOCOLATE) return;
		Player player = event.getPlayer();
		player.setStatistic(Statistic.TIME_SINCE_REST, 0);
		SwissSMPler.get(player).sendActionBar(ChatColor.WHITE+"Du fühlst dich erholt.");
	}
}

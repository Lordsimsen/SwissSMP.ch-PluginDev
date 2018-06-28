package ch.swisssmp.customitems;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class EventListener implements Listener{
	@EventHandler
	private void onPrepareItemCraft(PrepareItemCraftEvent event){
		ItemStack itemStack = event.getInventory().getResult();
		if(itemStack==null) return;
		String customEnum = CustomItems.getCustomEnum(itemStack);
		if(customEnum==null) return;
		Recipe recipe = event.getRecipe();
		boolean allow;
		if(recipe instanceof ShapedRecipe){
			allow = CustomItems.checkIngredients((ShapedRecipe)recipe, event.getInventory());
		}
		else if(recipe instanceof ShapelessRecipe){
			allow = CustomItems.checkIngredients((ShapelessRecipe)recipe, event.getInventory());
		}
		else{
			return;
		}
		if(!allow) event.getInventory().setResult(null);
	}
}

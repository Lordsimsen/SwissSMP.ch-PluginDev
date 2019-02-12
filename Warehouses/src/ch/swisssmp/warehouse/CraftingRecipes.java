package ch.swisssmp.warehouse;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class CraftingRecipes {
	protected static void register(){
		registerStockLedger();
	}
	
	private static void registerStockLedger(){
		ItemStack result = ItemManager.getWarehouseTool();
		if(result==null) return;
		NamespacedKey key = new NamespacedKey(WarehousesPlugin.getInstance(), "stock_ledger");
		ShapedRecipe recipe = new ShapedRecipe(key, result);
		recipe.setGroup("tools");
		recipe.shape("geg","ppp","grg");
		recipe.setIngredient('g', Material.GOLD_INGOT);
		recipe.setIngredient('e', Material.ENDER_EYE);
		recipe.setIngredient('p', Material.PAPER);
		recipe.setIngredient('r', Material.COMPARATOR);
		Bukkit.addRecipe(recipe);
	}	
}

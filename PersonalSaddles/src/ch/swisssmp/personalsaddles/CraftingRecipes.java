package ch.swisssmp.personalsaddles;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class CraftingRecipes {
	public static void register(){
		registerPrivateSaddle();
	}
	
	private static void registerPrivateSaddle(){
		SaddleInfo saddleInfo = new SaddleInfo(null,null);
		ItemStack itemStack = new ItemStack(Material.SADDLE);
		saddleInfo.apply(itemStack);
		NamespacedKey key = new NamespacedKey(PersonalSaddlesPlugin.getInstance(), "personal_saddle");
		ShapedRecipe recipe = new ShapedRecipe(key, itemStack);
		recipe.shape(" n ","lsl"," i ");
		recipe.setIngredient('n', Material.NAME_TAG);
		recipe.setIngredient('l', Material.LEAD);
		recipe.setIngredient('s', Material.SADDLE);
		recipe.setIngredient('i', Material.IRON_INGOT);
		Bukkit.addRecipe(recipe);
	}
}

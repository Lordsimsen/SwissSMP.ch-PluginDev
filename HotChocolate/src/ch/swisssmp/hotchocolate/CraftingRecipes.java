package ch.swisssmp.hotchocolate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.SmokingRecipe;

public class CraftingRecipes {
	protected static NamespacedKey getPowderKey() {
		return new NamespacedKey(HotChocolatePlugin.getInstance(), "chocolate_powder");
	}
	
	protected static NamespacedKey getHotChocolateKey() {
		return new NamespacedKey(HotChocolatePlugin.getInstance(), "hot_chocolate");
	}
	
	protected static void reload() {
		reloadPowderRecipe();
		reloadHotChocolateRecipe();
	}
	
	@SuppressWarnings("deprecation")
	private static void reloadPowderRecipe() {
		String prefix = HotChocolatePlugin.getPrefix();
		NamespacedKey key = getPowderKey();
		Bukkit.removeRecipe(key);
		ItemStack itemStack = CustomMaterial.CHOCOLATE_POWDER.getItemStack();
		if(itemStack==null) {
			Bukkit.getLogger().info(prefix+ChatColor.RED+" Konnte ItemStack für "+CustomMaterial.CHOCOLATE_POWDER+" nicht konstruieren");
			return;
		}
		itemStack.setAmount(1);
		RecipeChoice choice = new RecipeChoice.ExactChoice(new ItemStack(Material.COCOA_BEANS));
		SmokingRecipe smokingRecipe = new SmokingRecipe(key, itemStack, choice, 0.35f, 20*20); // 20s * 20fps
		Bukkit.addRecipe(smokingRecipe);
		FurnaceRecipe smeltingRecipe = new FurnaceRecipe(key, itemStack, choice, 0.7f, 40*20); // 40s * 20fps
		Bukkit.addRecipe(smeltingRecipe);
	}
	
	@SuppressWarnings("deprecation")
	private static void reloadHotChocolateRecipe() {
		String prefix = HotChocolatePlugin.getPrefix();
		NamespacedKey key = getHotChocolateKey();
		Bukkit.removeRecipe(key);
		ItemStack itemStack = CustomMaterial.HOT_CHOCOLATE.getItemStack();
		ItemStack powderStack = CustomMaterial.CHOCOLATE_POWDER.getItemStack();
		if(itemStack==null) {
			Bukkit.getLogger().info(prefix+ChatColor.RED+" Konnte ItemStack für "+CustomMaterial.HOT_CHOCOLATE+" nicht konstruieren");
			return;
		}
		if(powderStack==null) {
			Bukkit.getLogger().info(prefix+ChatColor.RED+" Konnte ItemStack für "+CustomMaterial.CHOCOLATE_POWDER+" nicht konstruieren");
			return;
		}
		itemStack.setAmount(1);
		ShapedRecipe recipe = new ShapedRecipe(key, itemStack);
		recipe.shape(" s ", " p ", " m ");
		recipe.setIngredient('s', Material.SUGAR);
		recipe.setIngredient('p', new RecipeChoice.ExactChoice(powderStack));
		recipe.setIngredient('m', Material.MILK_BUCKET);
		Bukkit.addRecipe(recipe);
	}
}

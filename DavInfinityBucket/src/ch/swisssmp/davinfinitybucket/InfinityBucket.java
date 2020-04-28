package ch.swisssmp.davinfinitybucket;

import ch.swisssmp.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class InfinityBucket {

	private static final String DATA_PROPERTY = "InfinityBucket";

	protected static void createRecipe() {

		NamespacedKey key = new NamespacedKey(DavInfinityBucketPlugin.getInstance(), "infinity_bucket");
		ItemStack itemStack = new ItemStack(Material.WATER_BUCKET);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(Collections.singletonList(ChatColor.YELLOW + "Unendlich verwendbar"));
		itemStack.setItemMeta(itemMeta);
		ItemUtil.setBoolean(itemStack, DATA_PROPERTY, true);
		ShapelessRecipe recipe = new ShapelessRecipe(key, itemStack);

			recipe.addIngredient(Material.WATER_BUCKET);
			recipe.addIngredient(Material.GHAST_TEAR);

			Bukkit.addRecipe(recipe);
 	}

 	public static boolean isInfinityBucket(ItemStack itemStack) {
		return itemStack != null && ItemUtil.getBoolean(itemStack, DATA_PROPERTY);
	}
}

package ch.swisssmp.trophies;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import ch.swisssmp.customitems.CustomItems;

public class CustomItemController {
	
	private static NamespacedKey[] keys;
	
	protected static void registerRecipe() {
		String prefix = TrophyPedestalsPlugin.getPrefix();
		NamespacedKey[] keys = new NamespacedKey[16];
		Color[] colors = Color.values();
		for(int i = 0; i < colors.length; i++) {
			Color color = colors[i];
			NamespacedKey key = new NamespacedKey(TrophyPedestalsPlugin.getInstance(), color.getCustomItemEnum().toLowerCase());
			ItemStack result = color.getItemStack();
			if(result==null || result.getType()==Material.AIR) {
				Bukkit.getLogger().info(prefix+ChatColor.RED+" Konnte das Rezept für "+key.getKey()+" nicht generieren!");
				continue;
			}
			ShapedRecipe recipe = new ShapedRecipe(key, result);
			recipe.shape("sws"," s ","hhh");
			recipe.setIngredient('s', Material.STONE);
			recipe.setIngredient('w', color.getMaterial());
			recipe.setIngredient('h', Material.SMOOTH_STONE_SLAB);
			
			// Bukkit.getLogger().info(prefix+ChatColor.GREEN+" Rezept für "+key.getKey()+" registriert! Wolle: "+color.getMaterial().toString());
			
			Bukkit.addRecipe(recipe);
			keys[i] = key;
		}
		CustomItemController.keys = keys;
	}
	
	protected static void unregisterRecipe() {
		if(keys==null) return;
		for(NamespacedKey key : keys) {
			Bukkit.removeRecipe(key);
		}
	}
	
	public static boolean isTrophyPedestal(ItemStack itemStack) {
		String customEnum = CustomItems.getCustomEnum(itemStack);
		if(customEnum==null) return false;
		for(Color color : Color.values()) {
			if(!customEnum.equals(color.getCustomItemEnum())) continue;
			return true;
		}
		
		return false;
	}
}

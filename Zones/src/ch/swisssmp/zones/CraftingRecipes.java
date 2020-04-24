package ch.swisssmp.zones;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

class CraftingRecipes {
	static void register(){
		registerGenericZoneMap();
		registerProjectZoneMap();
		registerAntiCreeperZoneMap();
		registerAntiHostileZoneMap();
		registerInversionZoneMap();
	}

	private static void registerGenericZoneMap(){
		ItemStack itemStack = ZoneType.GENERIC.toItemStack();
		NamespacedKey key = new NamespacedKey(ZonesPlugin.getInstance(), "generic_zone_map");
		ShapedRecipe recipe = new ShapedRecipe(key, itemStack);
		recipe.setGroup("tools");
		recipe.shape("rrr", "rmr", "rrr");
		recipe.setIngredient('m', Material.MAP);
		recipe.setIngredient('r', Material.REDSTONE_TORCH);
		Bukkit.addRecipe(recipe);
	}
	
	private static void registerProjectZoneMap(){
		ItemStack itemStack = ZoneType.PROJECT.toItemStack();
		NamespacedKey key = new NamespacedKey(ZonesPlugin.getInstance(), "project_zone_map");
		ShapedRecipe recipe = new ShapedRecipe(key, itemStack);
		recipe.setGroup("tools");
		recipe.shape("r r", " m ", "r r");
		recipe.setIngredient('r', Material.REDSTONE_TORCH);
		recipe.setIngredient('m', Material.MAP);
		Bukkit.addRecipe(recipe);
	}
	
	private static void registerAntiCreeperZoneMap(){
		ItemStack itemStack = ZoneType.NO_CREEPER.toItemStack();
		NamespacedKey key = new NamespacedKey(ZonesPlugin.getInstance(), "anti_creeper_zone_map");
		ShapedRecipe recipe = new ShapedRecipe(key, itemStack);
		recipe.setGroup("tools");
		recipe.shape("igi", "gmg", "igi");
		recipe.setIngredient('i', Material.IRON_INGOT);
		recipe.setIngredient('g', Material.GOLD_INGOT);
		recipe.setIngredient('m', Material.MAP);
		Bukkit.addRecipe(recipe);
	}
	
	private static void registerAntiHostileZoneMap(){
		ItemStack itemStack = ZoneType.NO_HOSTILE.toItemStack();
		NamespacedKey key = new NamespacedKey(ZonesPlugin.getInstance(), "anti_hostile_zone_map");
		ShapedRecipe recipe = new ShapedRecipe(key, itemStack);
		recipe.setGroup("tools");
		recipe.shape("odo", "dmd", "odo");
		recipe.setIngredient('o', Material.OBSIDIAN);
		recipe.setIngredient('d', Material.DIAMOND);
		recipe.setIngredient('m', Material.MAP);
		Bukkit.addRecipe(recipe);
	}
	
	private static void registerInversionZoneMap(){
		ItemStack itemStack = ZoneType.ALLOW_SPAWN.toItemStack();
		NamespacedKey key = new NamespacedKey(ZonesPlugin.getInstance(), "inversion_zone_map");
		ShapedRecipe recipe = new ShapedRecipe(key, itemStack);
		recipe.setGroup("tools");
		recipe.shape("iri", "rmr", "iri");
		recipe.setIngredient('i', Material.IRON_INGOT);
		recipe.setIngredient('r', Material.REDSTONE);
		recipe.setIngredient('m', Material.MAP);
		Bukkit.addRecipe(recipe);
	}
}

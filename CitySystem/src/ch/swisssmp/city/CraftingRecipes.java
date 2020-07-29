package ch.swisssmp.city;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;


class CraftingRecipes {
	
	private static ShapedRecipe citizenBillRecipe;
	
	protected static void register(){
		registerCitizenBill();
		System.out.println("Registriere Rezept!");
	}
	
	@SuppressWarnings({ "deprecation" })
	private static void registerCitizenBill(){
		ItemStack ringStack = ItemManager.createRing("METAL_RING");
		ItemStack result = ItemManager.createCitizenBill(new CitizenBill());
		citizenBillRecipe = new ShapedRecipe(new NamespacedKey(CitySystemPlugin.getInstance(),"citizen_bill"), result);
		citizenBillRecipe.setGroup("Städtesystem");
		citizenBillRecipe.shape("epe","prp","epe");
		citizenBillRecipe.setIngredient('e', Material.EMERALD);
		citizenBillRecipe.setIngredient('p', Material.PAPER);
		citizenBillRecipe.setIngredient('r', ringStack.getType(), Short.MAX_VALUE);
		citizenBillRecipe.getChoiceMap();
		Bukkit.addRecipe(citizenBillRecipe);
	}
}

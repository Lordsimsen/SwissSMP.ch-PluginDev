package ch.swisssmp.city;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;


class CraftingRecipes {
	
	private static ShapedRecipe citizenBillRecipe;
	
	protected static void register(){
		registerCitizenBill();
		System.out.println("Registriere Rezept!");
	}

	protected static void unregister(){
		if(citizenBillRecipe!=null) Bukkit.removeRecipe(citizenBillRecipe.getKey());
	}

	@SuppressWarnings({ "deprecation" })
	private static void registerCitizenBill(){
		ItemStack ringStack = SigilRingType.METAL_RING.createItemStack();
		ItemStack result = CitizenBill.EMPTY.createItemStack();
		citizenBillRecipe = new ShapedRecipe(new NamespacedKey(CitySystemPlugin.getInstance(),"citizen_bill"), result);
		citizenBillRecipe.setGroup("Städtesystem");
		citizenBillRecipe.shape("epe","prp","epe");
		citizenBillRecipe.setIngredient('e', Material.EMERALD);
		citizenBillRecipe.setIngredient('p', Material.PAPER);
		citizenBillRecipe.setIngredient('r', new RecipeChoice.ExactChoice(ringStack));
		citizenBillRecipe.getChoiceMap();
		Bukkit.addRecipe(citizenBillRecipe);
	}
}

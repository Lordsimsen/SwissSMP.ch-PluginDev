package ch.swisssmp.customitems;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.swisssmp.utils.*;
import com.google.gson.JsonElement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ch.swisssmp.webcore.DataSource;

public class CustomItems{

	private static final CustomItemBuilder INVALID_BUILDER = new CustomItemBuilder(Material.BARRIER).setDisplayName("???");

	protected static void reload(){
		CustomMaterialTemplates.load();
		CustomItemTemplates.load();
	}

	/**
	 * Setzt den customEnum vom angegebenen ItemStack ohne weitere Eigenschaften zu verändern.
	 */
	public static void setCustomEnum(ItemStack itemStack, String customEnum){
		if(itemStack==null)return;
		ItemUtil.setString(itemStack, "customEnum", customEnum);
	}

	/**
	 * Liest den customEnum vom angegebenen ItemStack.
	 * @param itemStack - Der zu analysierende ItemStack.
	 * @return Den gefundenen customEnum oder <code>null</code>.
	 */
	public static String getCustomEnum(ItemStack itemStack){
		if(itemStack==null) return null;
		return ItemUtil.getString(itemStack, "customEnum");
	}

	public static NamespacedKey getKey(ItemStack itemStack){
		String customEnum = getCustomEnum(itemStack);
		if(customEnum==null) return null;
		if(customEnum.contains(":")){
			String[] parts = customEnum.split(":");
			//noinspection deprecation
			return new NamespacedKey(parts[0].toLowerCase(), parts[1].toLowerCase());
		}

		return NamespacedKey.minecraft(customEnum.toLowerCase());
	}

	public static CustomItemBuilder getCustomItemBuilder(String customEnum){
		return getCustomItemBuilder(customEnum, 1);
	}
	
	/**
	 * Generiert ein Factory Objekt aufgrund einer voreingestellten Konfiguration, identifiziert durch den customEnum.
	 * Der generierte ItemStack wird ohne weitere Einstellung eine Menge von 1 haben.
	 * @param key - Entspricht dem Wert aus dem Web-Interface
	 * @return Ein CustomItemBuilder mit allen Voreinstellungen für den angegebenen CustomEnum.
	 */
	public static CustomItemBuilder getCustomItemBuilder(NamespacedKey key){
		return CustomItems.getCustomItemBuilder(key, 1);
	}

	public static CustomItemBuilder getCustomItemBuilder(String customEnum, int amount){
		NamespacedKey key;
		if(customEnum.contains(":")){
			String[] parts = customEnum.split(":");
			//noinspection deprecation
			key = new NamespacedKey(parts[0].toLowerCase(), parts[1].toLowerCase());
		}
		else{
			key = NamespacedKey.minecraft(customEnum.toLowerCase());
		}
		return getCustomItemBuilder(key, amount);
	}

	/**
	 * Generiert ein Factory Objekt aufgrund einer voreingestellten Konfiguration, identifiziert durch den customEnum.
	 * Der generierte ItemStack wird die angegebene Grösse haben.
	 * @param key - Entspricht dem Wert aus dem Web-Interface
	 * @param amount - Setzt die Grösse
	 * @return Ein CustomItemBuilder mit allen Voreinstellungen für den angegebenen CustomEnum und der festgelegten Grösse.
	 */
	public static CustomItemBuilder getCustomItemBuilder(NamespacedKey key, int amount){
		IBuilderTemplate template = CustomItemTemplate.get(key).orElse(null);
		if(template==null) template = CustomMaterialTemplate.get(key).orElse(null);
		if(template==null) return INVALID_BUILDER;
		JsonObject json = template.getData();
		CustomItemBuilder result = new CustomItemBuilder();
		if(template instanceof CustomMaterialTemplate){
			result.setCustomEnum(JsonUtil.getString("custom_enum", json), (CustomMaterialTemplate) template);
		}
		else{
			result.setCustomEnum(JsonUtil.getString("custom_enum", json));
		}
		getCustomItemBuilder(json, result);
		result.setAmount(amount);
		return result;
	}

	/**
	 * Generiert ein Factory Objekt aufgrund einer ConfigurationSection.
	 * @param json - Ein JsonObject mit allen zu setzenden Werten
	 * @return Ein CustomItemBuilder mit allen Voreinstellungen aus der dataSection.
	 */
	public static CustomItemBuilder getCustomItemBuilder(JsonObject json){
		int amount = Math.max(1, JsonUtil.getInt("amount", json));
		return getCustomItemBuilder(json, amount);
	}

	/**
	 * Generiert ein Factory Objekt aufgrund einer ConfigurationSection und einer Grösse.
	 * @param json - Ein JsonObject mit allen zu setzenden Werten
	 * @param amount - Setzt die Grösse des resultierenden ItemStacks
	 * @return Ein CustomItemBuilder mit allen Voreinstellungen aus der dataSection.
	 */
	public static CustomItemBuilder getCustomItemBuilder(JsonObject json, int amount){
		if(json==null) return INVALID_BUILDER;
		CustomItemBuilder customItemBuilder;
		if(json.has("custom_enum")){
			customItemBuilder = getCustomItemBuilder(JsonUtil.getString("custom_enum", json));
		}
		else{
			customItemBuilder = new CustomItemBuilder();
		}
		if(customItemBuilder==null) return INVALID_BUILDER;
		getCustomItemBuilder(json, customItemBuilder);
		customItemBuilder.setAmount(amount);
		return customItemBuilder;
	}

	/**
	 * Wendet alle Einstellungen aus der ConfigurationSection auf den CustomItemBuilder an.
	 * @param json - Ein JsonObject mit allen zu setzenden Werten
	 * @param customItemBuilder - Der zu modifizierende CustomItemBuilder
	 * @return Der angegebene CustomItemBuilder, zur vereinfachten Verkettung von Befehlen.
	 */
	public static CustomItemBuilder getCustomItemBuilder(JsonObject json, CustomItemBuilder customItemBuilder){
		if(json==null) return customItemBuilder;
		for(Map.Entry<String, JsonElement> entry : json.entrySet()){
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			switch(key.toLowerCase()){
			case "custom_enum":{
				//should already be applied
				break;
			}
			case "material":{
				Material material = JsonUtil.getMaterial(value);
				if(material==null) Bukkit.getLogger().warning("[CustomItems] Material "+value+" ist ungültig.");
				else customItemBuilder.setMaterial(material);
				break;
			}
			case "amount":{
				customItemBuilder.setAmount(value.getAsInt());
				break;
			}
			case "durability":{
				customItemBuilder.setDurability(value.getAsShort());
				break;
			}
			case "custom_durability":{
				customItemBuilder.setCustomDurability(value.getAsInt());
				break;
			}
			case "custom_model_id":{
				customItemBuilder.setCustomModelId(value.getAsInt());
				break;
			}
			case "use_custom_model_data_property":
				customItemBuilder.setUseCustomModelDataProperty(value.getAsBoolean());
				break;
			case "max_custom_durability":{
				customItemBuilder.setMaxCustomDurability(value.getAsInt());
				break;
			}
			case "enchantment":{
				customItemBuilder.addEnchantment(JsonUtil.getEnchantmentData(value.getAsJsonObject()));
				break;
			}
			case "enchantments":{
				JsonArray enchantmentsArray = value.getAsJsonArray();
				for(JsonElement element : enchantmentsArray){
					if(!element.isJsonObject()) continue;
					JsonObject enchantmentSection = element.getAsJsonObject();
					if(enchantmentSection.has("probability")){
						if(ThreadLocalRandom.current().nextDouble()>JsonUtil.getDouble("probability", enchantmentSection)) continue;
					}
					EnchantmentData enchantmentData = JsonUtil.getEnchantmentData(enchantmentSection);
					if(enchantmentData==null){
						Bukkit.getLogger().warning("[CustomItems] Enchantment '"+element+"' ist ungültig.");
						continue;
					}
					customItemBuilder.addEnchantment(enchantmentData);
				}
				break;
			}
			case "item_flag":{
				ItemFlag flag;
				try{
					flag = ItemFlag.valueOf(value.getAsString().toUpperCase());
				}
				catch(Exception ignored){
					Bukkit.getLogger().warning("[CustomItems] ItemFlag '"+value+"' ist ungültig.");
					break;
				}
				customItemBuilder.addItemFlags(flag);
				break;
			}
			case "item_flags":{
				for(String itemFlag : JsonUtil.getStringList(value.getAsJsonArray())){
					try{
						customItemBuilder.addItemFlags(ItemFlag.valueOf(itemFlag));
					}
					catch(Exception e){
						Bukkit.getLogger().warning("[CustomItems] Ungültige Item Flag "+itemFlag+"!");
					}
				}
				break;
			}
			case "display_name":{
				customItemBuilder.setDisplayName(value.getAsString());
				break;
			}
			case "localized_name":{
				customItemBuilder.setLocalizedName(value.getAsString());
				break;
			}
			case "lore":{
				customItemBuilder.setLore(JsonUtil.getStringList(value.getAsJsonArray()));
				break;
			}
			case "unbreakable":{
				customItemBuilder.setUnbreakable(value.getAsBoolean());
				break;
			}
			case "attack_damage":{
				customItemBuilder.setAttackDamage(value.getAsDouble());
				break;
			}
			case "attack_speed":{
				customItemBuilder.setAttackSpeed(value.getAsDouble());
				break;
			}
			case "max_health":{
				customItemBuilder.setMaxHealth(value.getAsDouble());
				break;
			}
			case "armor":{
				customItemBuilder.setArmor(value.getAsDouble());
				break;
			}
			case "movement_speed":{
				customItemBuilder.setMovementSpeed(value.getAsDouble());
				break;
			}
			case "luck":{
				customItemBuilder.setLuck(value.getAsDouble());
				break;
			}
			case "custom_potion_color":{
				customItemBuilder.setCustomPotionColor(value.getAsInt());
				break;
			}
			case "skull_owner":{
				try{
					UUID owner = UUID.fromString(value.getAsString());
					customItemBuilder.setSkullOwner(owner);
				}
				catch(Exception e){
					Bukkit.getLogger().warning("[CustomItems] Invalid Skull Owner: "+value.getAsString());
				}
				break;
			}
			//these properties do not matter to the generation of the item builder
			case "use_item_id":
				//sets whether the item_id should be included in the items NMS tags (is checked when the item_id is set)
			case "signature":
				//if not declared but item_id is included the item builder will fetch linked data from the web-interface
			case "probability":{
				//probability can be used outside to add a chance of generating an item at all
				break;
			}
			case "expiration_date":{
				customItemBuilder.setExpirationDate(value.getAsInt());
				break;
			}
			case "max_stack_size":{
				customItemBuilder.setMaxStackSize(value.getAsInt());
				break;
			}
			default:{
				// Bukkit.getLogger().warning("[CustomItems] Unkown item property '"+key+"'");
				break;
			}
			}
		}
		CreateCustomItemBuilderEvent event = new CreateCustomItemBuilderEvent(customItemBuilder, json);
		try {
			Bukkit.getPluginManager().callEvent(event);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		if(customItemBuilder.getMaterial()==null || customItemBuilder.getMaterial()==Material.AIR){
			Bukkit.getLogger().info("[CustomItems] ItemBuilder konnte nicht abgeschlossen werden, da kein Material angegeben wurde.");
			Bukkit.getLogger().info(json.toString());
			return INVALID_BUILDER;
		}
		return customItemBuilder;
	}

	/**
	 * Prüft die Crafting-Matrix auf alle Zutaten aus dem ShapedRecipe, unter Beachtung von CustomItems
	 * @param shape - Die Form aus dem ShapedRecipe
	 * @param ingredients - Die Zutaten aus dem ShapedRecipe
	 * @param inventory - Das Crafting Inventar
	 * @return <code>true</code>, wenn alle Zutaten exakt gleich sind, ansonsten <code>false</code>
	 */
	public static boolean checkIngredients(String[] shape, Map<Character,RecipeChoice> ingredients, CraftingInventory inventory){
		//String ingredientCustomEnum;
		ItemStack[] matrix = inventory.getMatrix();
		int matrixDimensions = matrix.length==9 ? 3 : 2; // 3x3 or 2x2 grid
		
		int height = shape.length;
		int width = getShapeWidth(shape);

		int recipeTop = getRecipeTop(shape);
		int recipeLeft = getRecipeLeft(shape);
		
		int top = getTopRow(matrix, matrixDimensions, matrixDimensions);
		int left = getLeftColumn(matrix, matrixDimensions, matrixDimensions);
		
		// Bukkit.getLogger().info(String.join(", ", shape));
		
		for(int y = top; y < top+height; y++){
			for(int x = left; x < left+width; x++){
				int recipeX = x-left+recipeLeft;
				int recipeY = y-top+recipeTop;
				ItemStack itemStack = matrix[x+y*matrixDimensions];
				char c = recipeY<shape.length && recipeX < shape[recipeY].length() ? shape[recipeY].charAt(recipeX) : ' ';
				if(c==' ' && itemStack==null) continue;
				
				if((c==' ' && itemStack!=null)) {
					// Bukkit.getLogger().info("Slot should be "+c+", found "+(itemStack==null ? null : itemStack.getType()));
					return false;
				}
				
				RecipeChoice ingredient = ingredients.get(c);
				if(ingredient==null) {
					if(itemStack==null) continue;
					// Bukkit.getLogger().info("Slot should empty, found "+itemStack.getType());
					return false;
				}
				
				if(itemStack==null) {
					// Bukkit.getLogger().info("Slot should not be empty");
					return false;
				}
				
				if(!ingredient.test(itemStack)) {
					// Bukkit.getLogger().info("Stacks are not similar");
					return false;
				}
				
				if((ingredient instanceof RecipeChoice.MaterialChoice) && (itemStack.getItemMeta().hasCustomModelData() || CustomItems.getCustomEnum(itemStack)!=null)) {
					// Bukkit.getLogger().info("Provided stack contains CustomModelData");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Prüft die Crafting-Matrix auf alle Zutaten aus dem ShapelessRecipe, unter Beachtung von CustomItems
	 * @param recipe - Das Crafting Rezept
	 * @param inventory - Das Crafting Inventar
	 * @return <code>true</code>, wenn alle Zutaten exakt gleich sind, ansonsten <code>false</code>
	 */
	public static boolean checkIngredients(ShapelessRecipe recipe, CraftingInventory inventory){
		
		for(RecipeChoice choice : recipe.getChoiceList()){
			
			boolean foundIngredient = false;
			boolean checkCustomModelData = choice instanceof RecipeChoice.MaterialChoice;
			for(ItemStack itemStack : inventory.getMatrix()){
				if(itemStack==null) {
					continue;
				}
				
				if(!choice.test(itemStack)){
					continue;
				}
				
				int customModelDataA = itemStack.getItemMeta().hasCustomModelData() ? itemStack.getItemMeta().getCustomModelData() : -1;
				if(checkCustomModelData && customModelDataA>0) {
					continue;
				}
				foundIngredient = true;
				break;
			}
			if(foundIngredient) continue;
			return false;
		}
		return true;
	}

	/**
	 * Prüft, ob das zu schmelzende Item exakt dem FurnaceRecipe entspricht, unter Beachtung von CustomItems
	 * @param recipe - Das Rezept
	 * @param inventory - Das Inventar
	 * @return <code>true</code>, wenn die Zutat exakt gleich ist, ansonsten <code>false</code>
	 */
	public static boolean checkIngredients(FurnaceRecipe recipe, FurnaceInventory inventory){
		ItemStack ingredient = recipe.getInput();
		for(ItemStack itemStack : inventory){
			if(itemStack==null)continue;
			if(itemStack.isSimilar(ingredient)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Prüft, ob die gebotenen Items exakt dem Handel entsprechen, unter Beachtung von CustomItems
	 * @param recipe - Das Rezept
	 * @param inventory - Das Inventar
	 * @return <code>true</code>, wenn das Angebot exakt gleich ist, ansonsten <code>false</code>
	 */
	public static boolean checkIngredients(MerchantRecipe recipe, MerchantInventory inventory){
		outer:
		for(ItemStack ingredient : recipe.getIngredients()){
			for(ItemStack itemStack : inventory){
				if(itemStack==null)continue;
				if(itemStack.isSimilar(ingredient)){
					continue outer;
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * Entfernt alle abgelaufenen Items aus dem Inventar. Ob ein ItemStack ein Ablaufdatum hat, und wann dieses ist, wird durch den NBT-Wert 'expirationDate' festgelegt.
	 * @param inventory - Das zu prüfende Inventar
	 */
	public static void clearExpiredItems(Inventory inventory){
		long currentTime = System.currentTimeMillis()/1000;
		long expirationDate;
		for(ItemStack itemStack : inventory){
			if(itemStack==null) continue;
			expirationDate = ItemUtil.getInt(itemStack, "expirationDate");
			if(expirationDate==0 || expirationDate>currentTime) continue;
			itemStack.setAmount(0);
		}
	}
	
	private static int getShapeWidth(String[] shape) {
		int max = 1;
		for(int i = 0; i < shape.length; i++) {
			max = Math.max(shape[i].trim().length(), max);
		}
		return max;
	}
	
	private static int getTopRow(ItemStack[] matrix, int rows, int columns) {
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < columns; x++) {
				ItemStack itemStack = matrix[y*columns+x];
				if(itemStack!=null) return y;
			}
		}
		return 0;
	}
	
	private static int getLeftColumn(ItemStack[] matrix, int rows, int columns) {
		for(int x = 0; x < columns; x++) {
			for(int y = 0; y < rows; y++) {
				ItemStack itemStack = matrix[y*columns+x];
				if(itemStack!=null) return x;
			}
		}
		return 0;
	}
	
	private static int getRecipeTop(String[] shape) {
		for(int y = 0; y < shape.length; y++) {
			for(int x = 0; x < shape[y].length(); x++) {
				char c = shape[y].charAt(x);
				if(c!=' ') return y;
			}
		}
		return 0;
	}
	
	private static int getRecipeLeft(String[] shape) {
		for(int x = 0; x < 3; x++) {
			for(int y = 0; y < shape.length; y++) {
				if(x>=shape[y].length()) continue;
				char c = shape[y].charAt(x);
				if(c!=' ') return x;
			}
		}
		return 0;
	}
	
	protected static void uploadData(){
		Material[] materials = Material.values();
		JsonObject data = new JsonObject();
		JsonArray materialsArray = new JsonArray();
		for(int i = 0; i < materials.length; i++){
			Material material = materials[i];
			JsonObject materialSection = new JsonObject();
			materialSection.addProperty("namespace", URLEncoder.encode(material.getKey().getNamespace()));
			materialSection.addProperty("key", URLEncoder.encode(material.getKey().getKey()));
			materialSection.addProperty("name", URLEncoder.encode(material.name()));
			materialSection.addProperty("max_durability", material.getMaxDurability());
			materialSection.addProperty("max_stack_size", material.getMaxStackSize());
			materialSection.addProperty("type", (material.isBlock() ? "block" : "item"));
			String group;
			if(material.isEdible()){
				group = "FOOD";
			}
			else if(material.isFuel()){
				group = "FUEL";
			}
			else if(material.isInteractable()){
				group = "INTERACTABLE";
			}
			else if(material.isRecord()){
				group = "RECORD";
			}
			else{
				group = "";
			}
			materialSection.addProperty("group", group);
			materialsArray.add(materialSection);
		}
		data.add("items", materialsArray);
		DataSource.getResponse(CustomItemsPlugin.getInstance(), "upload_material_data.php", new String[]{
				"data="+Base64.encodeBase64URLSafeString(data.toString().getBytes())
		});
	}
}

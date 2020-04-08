package ch.swisssmp.customitems;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.EnchantmentData;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;

public class CustomItems extends JavaPlugin{
	private static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static CustomItems plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("customitems").setExecutor(playerCommand);
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		reload();
		
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	protected static void reload(){
		CustomMaterialTemplates.load();
		CustomItemTemplates.load();
	}
	
	public static String getCustomEnum(ItemStack itemStack){
		if(itemStack==null)return null;
		return ItemUtil.getString(itemStack, "customEnum");
	}
	
	public static CustomItemBuilder getCustomItemBuilder(String custom_enum){
		return CustomItems.getCustomItemBuilder(custom_enum, 1);
	}
	
	public static CustomItemBuilder getCustomItemBuilder(String custom_enum, int amount){
		IBuilderTemplate template = CustomItemTemplate.get(custom_enum);
		if(template==null) template = CustomMaterialTemplate.get(custom_enum);
		if(template==null) return null;
		ConfigurationSection dataSection = template.getData();
		CustomItemBuilder result = new CustomItemBuilder();
		if(template instanceof CustomMaterialTemplate){
			result.setCustomEnum(dataSection.getString("custom_enum"), (CustomMaterialTemplate) template);
		}
		else{
			result.setCustomEnum(dataSection.getString("custom_enum"));
		}
		getCustomItemBuilder(dataSection, result);
		result.setAmount(amount);
		return result;
	}
	
	public static CustomItemBuilder getCustomItemBuilder(ConfigurationSection dataSection){
		int amount = dataSection.contains("amount") ? dataSection.getInt("amount") : 1;
		return getCustomItemBuilder(dataSection, amount);
	}
	
	public static CustomItemBuilder getCustomItemBuilder(ConfigurationSection dataSection, int amount){
		if(dataSection==null) return null;
		CustomItemBuilder customItemBuilder;
		if(dataSection.contains("custom_enum")){
			customItemBuilder = getCustomItemBuilder(dataSection.getString("custom_enum"));
		}
		else{
			customItemBuilder = new CustomItemBuilder();
		}
		if(customItemBuilder==null) return null;
		getCustomItemBuilder(dataSection, customItemBuilder);
		customItemBuilder.setAmount(amount);
		return customItemBuilder;
	}
	
	public static CustomItemBuilder getCustomItemBuilder(ConfigurationSection dataSection, CustomItemBuilder customItemBuilder){
		if(dataSection==null) return customItemBuilder;
		for(String key : dataSection.getKeys(false)){
			switch(key.toLowerCase()){
			case "custom_enum":{
				//should already be applied
				break;
			}
			case "material":{
				Material material = dataSection.getMaterial("material");
				if(material==null) Bukkit.getLogger().info("[CustomItems] Material "+dataSection.getString("material")+" ist ungültig.");
				else customItemBuilder.setMaterial(material);
				break;
			}
			case "amount":{
				customItemBuilder.setAmount(dataSection.getInt("amount"));
				break;
			}
			case "durability":{
				customItemBuilder.setDurability((short)dataSection.getInt("durability"));
				break;
			}
			case "custom_durability":{
				customItemBuilder.setCustomDurability(dataSection.getInt("custom_durability"));
				break;
			}
			case "custom_model_id":{
				customItemBuilder.setCustomModelId(dataSection.getInt("custom_model_id"));
				break;
			}
			case "use_custom_model_data_property":
				customItemBuilder.setUseCustomModelDataProperty(dataSection.getBoolean("use_custom_model_data_property"));
				break;
			case "max_custom_durability":{
				customItemBuilder.setMaxCustomDurability(dataSection.getInt("max_custom_durability"));
				break;
			}
			case "enchantment":{
				customItemBuilder.addEnchantment(dataSection.getEnchantmentData("enchantment"));
				break;
			}
			case "enchantments":{
				ConfigurationSection enchantmentsSection = dataSection.getConfigurationSection("enchantments");
				ConfigurationSection enchantmentSection;
				EnchantmentData enchantmentData;
				for(String enchantmentKey : enchantmentsSection.getKeys(false)){
					enchantmentSection = enchantmentsSection.getConfigurationSection(enchantmentKey);
					if(enchantmentSection.contains("probability")){
						if(ThreadLocalRandom.current().nextDouble()>enchantmentSection.getDouble("probability")) continue;
					}
					enchantmentData = enchantmentSection.getEnchantmentData();
					if(enchantmentData==null){
						Bukkit.getLogger().info("[CustomItems] Enchantment '"+enchantmentsSection.getConfigurationSection(enchantmentKey).getString("enchantment")+"' ist ungültig.");
						continue;
					}
					customItemBuilder.addEnchantment(enchantmentData);
				}
				break;
			}
			case "item_flag":{
				ItemFlag itemFlag = dataSection.getItemFlag("item_flag");
				if(itemFlag!=null){
					customItemBuilder.addItemFlags(itemFlag);
				}
				break;
			}
			case "item_flags":{
				for(String itemFlag : dataSection.getStringList("item_flags")){
					try{
						customItemBuilder.addItemFlags(ItemFlag.valueOf(itemFlag));
					}
					catch(Exception e){
						Bukkit.getLogger().info("[CustomItems] Ungültige Item Flag "+itemFlag+"!");
					}
				}
				break;
			}
			case "display_name":{
				customItemBuilder.setDisplayName(dataSection.getString("display_name"));
				break;
			}
			case "localized_name":{
				customItemBuilder.setLocalizedName(dataSection.getString("localized_name"));
				break;
			}
			case "lore":{
				customItemBuilder.setLore(dataSection.getStringList("lore"));
				break;
			}
			case "unbreakable":{
				customItemBuilder.setUnbreakable(dataSection.getBoolean("unbreakable"));
				break;
			}
			case "item_id":{
				if(dataSection.contains("use_item_id") && !dataSection.getBoolean("use_item_id")) continue;
				customItemBuilder.setItemId(dataSection.getInt("item_id"));
				break;
			}
			case "attack_damage":{
				customItemBuilder.setAttackDamage(dataSection.getDouble("attack_damage"));
				break;
			}
			case "attack_speed":{
				customItemBuilder.setAttackSpeed(dataSection.getDouble("attack_speed"));
				break;
			}
			case "max_health":{
				customItemBuilder.setMaxHealth(dataSection.getDouble("max_health"));
				break;
			}
			case "armor":{
				customItemBuilder.setArmor(dataSection.getDouble("armor"));
				break;
			}
			case "movement_speed":{
				customItemBuilder.setMovementSpeed(dataSection.getDouble("movement_speed"));
				break;
			}
			case "luck":{
				customItemBuilder.setLuck(dataSection.getDouble("luck"));
				break;
			}
			case "custom_potion_color":{
				customItemBuilder.setCustomPotionColor(dataSection.getInt("custom_potion_color"));
				break;
			}
			case "skull_owner":{
				try{
					UUID owner = UUID.fromString(dataSection.getString("skull_owner"));
					customItemBuilder.setSkullOwner(owner);
				}
				catch(Exception e){
					(new Exception("Invalid Skull Owner: "+dataSection.getString("skull_owner"))).printStackTrace();
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
				customItemBuilder.setExpirationDate(dataSection.getInt("expiration_date"));
				break;
			}
			case "max_stack_size":{
				customItemBuilder.setMaxStackSize(dataSection.getInt("max_stack_size"));
				break;
			}
			default:{
				Bukkit.getLogger().info("[CustomItems] Unkown item property '"+key+"'");
				break;
			}
			}
		}
		if(customItemBuilder.getMaterial()==null || customItemBuilder.getMaterial()==Material.AIR){
			Bukkit.getLogger().info("[CustomItems] ItemBuilder konnte nicht abgeschlossen werden, da kein Material angegeben wurde.");
			Bukkit.getLogger().info(dataSection.toString());
			return null;
		}
		return customItemBuilder;
	}

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
		DataSource.getResponse(CustomItems.getInstance(), "upload_material_data.php", new String[]{
				"data="+Base64.encodeBase64URLSafeString(data.toString().getBytes())
		});
	}
	
	public static CustomItems getInstance(){
		return plugin;
	}
}

package ch.swisssmp.customitems;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.EnchantmentData;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class CustomItems extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static CustomItems plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("customitems").setExecutor(playerCommand);
		this.getCommand("rename").setExecutor(new RenameCommand());
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
	}
	
	public static String getCustomEnum(ItemStack itemStack){
		if(itemStack==null)return null;
		return ItemUtil.getString(itemStack, "customEnum");
	}
	
	public static CustomItemBuilder getCustomItemBuilder(String custom_enum){
		return CustomItems.getCustomItemBuilder("items/material_builder.php", new String[]{
				"enum="+custom_enum
		});
	}
	
	public static CustomItemBuilder getCustomItemBuilder(String custom_enum, int amount){
		return CustomItems.getCustomItemBuilder("items/material_builder.php", new String[]{
				"enum="+custom_enum,
				"amount="+amount
		});
	}
	
	public static CustomItemBuilder getCustomItemBuilder(int item_id){
		return CustomItems.getCustomItemBuilder("items/item_builder.php", new String[]{
				"item="+item_id
		});
	}
	
	public static CustomItemBuilder getCustomItemBuilder(int item_id, int amount){
		return CustomItems.getCustomItemBuilder("items/item_builder.php", new String[]{
				"item="+item_id,
				"amount="+amount
		});
	}
	
	public static CustomItemBuilder getCustomItemBuilder(String url, String[] arguments){
		return getCustomItemBuilder(DataSource.getYamlResponse(url, arguments));
	}
	
	public static CustomItemBuilder getCustomItemBuilder(ConfigurationSection dataSection){
		if(dataSection==null) return null;
		if(dataSection.contains("item_id") && !dataSection.contains("signature")){
			YamlConfiguration baseSection = DataSource.getYamlResponse("items/item_builder.php", new String[]{
					"item="+dataSection.getInt("item_id")
			});
			if(baseSection!=null){
				for(String key : baseSection.getKeys(false)){
					if(!dataSection.contains(key)){
						dataSection.set(key, baseSection.get(key));
					}
				}
			}
		}
		CustomItemBuilder customItemBuilder = new CustomItemBuilder();
		for(String key : dataSection.getKeys(false)){
			switch(key.toLowerCase()){
			case "custom_enum":{
				customItemBuilder.setCustomEnum(dataSection.getString("custom_enum"));
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
				ConfigurationSection itemFlagsSection = dataSection.getConfigurationSection("item_flags");
				for(String itemFlagKey : itemFlagsSection.getKeys(false)){
					customItemBuilder.addItemFlags(itemFlagsSection.getItemFlag(itemFlagKey));
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
				customItemBuilder.setSkullOwner(dataSection.getString("skull_owner"));
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
			default:{
				logger.info("[CustomItems] Unkown item property '"+key+"'");
				break;
			}
			}
		}
		if(customItemBuilder.getMaterial()==null){
			Bukkit.getLogger().info("[CustomItems] ItemBuilder konnte nicht abgeschlossen werden, da kein Material angegeben wurde.");
			return null;
		}
		return customItemBuilder;
	}

	
	public static boolean checkIngredients(ShapedRecipe recipe, CraftingInventory inventory){
		//String ingredientCustomEnum;
		ItemStack itemStack;
		ItemStack ingredient;
		ItemStack[] matrix = inventory.getMatrix();
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				itemStack = matrix[x+y*3];
				if(itemStack==null) continue;
				ingredient = recipe.getIngredientMap().get(recipe.getShape()[y].charAt(x));
				if(ingredient==null) return false;
				if(!itemStack.isSimilar(ingredient)) return false;
			}
		}
		return true;
	}
	
	public static boolean checkIngredients(ShapelessRecipe recipe, CraftingInventory inventory){
		outer:
		for(ItemStack ingredient : recipe.getIngredientList()){
			for(ItemStack itemStack : inventory.getMatrix()){
				if(itemStack==null)continue;
				if(itemStack.isSimilar(ingredient)){
					continue outer;
				}
			}
			return false;
		}
		return true;
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

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}

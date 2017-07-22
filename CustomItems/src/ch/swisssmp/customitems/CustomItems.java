package ch.swisssmp.customitems;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.ConfigurationSection;
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
		
		//PlayerCommand playerCommand = new PlayerCommand();
		//this.getCommand("customitems").setExecutor(playerCommand);
		
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
	}
	
	public static CustomItemBuilder getCustomItemBuilder(String custom_enum){
		return CustomItems.getCustomItemBuilder("items/material_builder.php", new String[]{
				"enum="+custom_enum
		});
	}
	
	public static CustomItemBuilder getCustomItemBuilder(int item_id){
		return CustomItems.getCustomItemBuilder("items/item_builder.php", new String[]{
				"item="+item_id
		});
	}
	
	public static CustomItemBuilder getCustomItemBuilder(String url, String[] arguments){
		return getCustomItemBuilder(DataSource.getYamlResponse(url, arguments));
	}
	
	public static CustomItemBuilder getCustomItemBuilder(ConfigurationSection dataSection){
		if(dataSection==null) return null;
		CustomItemBuilder customItemBuilder = new CustomItemBuilder();
		for(String key : dataSection.getKeys(false)){
			switch(key.toLowerCase()){
			case "custom_enum":{
				customItemBuilder.setCustomEnum(dataSection.getString("custom_enum"));
				break;
			}
			case "material":{
				Material material = dataSection.getMaterial("material");
				if(material!=null) customItemBuilder.setMaterial(material);
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
			case "enchantment":{
				customItemBuilder.addEnchantment(dataSection.getEnchantmentData("enchantment"));
				break;
			}
			case "enchantments":{
				ConfigurationSection enchantmentsSection = dataSection.getConfigurationSection("enchantments");
				for(String enchantmentKey : enchantmentsSection.getKeys(false)){
					customItemBuilder.addEnchantment(enchantmentsSection.getEnchantmentData(enchantmentKey));
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
			default:{
				logger.info("[CustomItems] Unkown item property '"+key+"'");
				break;
			}
			}
		}
		return customItemBuilder;
	}
	

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}

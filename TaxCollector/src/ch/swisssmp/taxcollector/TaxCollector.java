package ch.swisssmp.taxcollector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class TaxCollector extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static TaxCollector plugin;
	protected static boolean debug;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		this.getCommand("tax").setExecutor(new ConsoleCommand());
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static void collect(){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("taxes/pending.php");
		if(yamlConfiguration==null) return;
		for(String key : yamlConfiguration.getKeys(false)){
			ConfigurationSection taxSection = yamlConfiguration.getConfigurationSection(key);
			int city_id = taxSection.getInt("city_id");
			int tax_id = taxSection.getInt("tax_id");
			HashMap<Material, Integer> resources = new HashMap<Material, Integer>();
			ConfigurationSection resourcesSection = taxSection.getConfigurationSection("resources");
			for(String resourceKey : resourcesSection.getKeys(false)){
				ConfigurationSection resourceSection = resourcesSection.getConfigurationSection(resourceKey);
				Material material = resourceSection.getMaterial("material");
				int amount = resourceSection.getInt("amount");
				resources.put(material, amount);
			}
			Location chestLocation = taxSection.getLocation("chest");
			if(chestLocation==null){
				continue;
			}
			Block block = chestLocation.getBlock();
			BlockState blockState = block.getState();
			if(blockState instanceof Chest){
				Inventory inventory = ((Chest)blockState).getInventory();
				for(ItemStack itemStack : inventory){
					if(itemStack==null) continue;
					if(resources.containsKey(itemStack.getType())){
						int required = resources.get(itemStack.getType());
						int take = Math.min(required, itemStack.getAmount());
						itemStack.setAmount(itemStack.getAmount()-take);
						resources.put(itemStack.getType(), required-take);
						Bukkit.getLogger().info("Removed "+take+" "+itemStack.getType().toString()+" from "+block.getX()+","+block.getY()+","+block.getZ()+" for the tax collection in city_"+city_id+".");
					}
				}
			}
			else{
				continue;
			}
			List<String> arguments = new ArrayList<String>();
			arguments.add("city_id="+city_id);
			arguments.add("tax_id="+tax_id);
			for(Entry<Material,Integer> entry : resources.entrySet()){
				arguments.add("resources["+entry.getKey().toString()+"]="+entry.getValue());
			}
			String[] argumentsArray = new String[arguments.size()];
			DataSource.getResponse("taxes/update.php", arguments.toArray(argumentsArray));
		}
	}
	
	public static void info(){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("taxes/chests.php");
		if(yamlConfiguration==null) return;
		for(String key : yamlConfiguration.getKeys(false)){
			ConfigurationSection taxSection = yamlConfiguration.getConfigurationSection(key);
			int addon_id = taxSection.getInt("addon_id");
			HashMap<String, Integer> resources = new HashMap<String, Integer>();
			Location chestLocation = taxSection.getLocation("chest");
			if(chestLocation==null){
				continue;
			}
			Block block = chestLocation.getBlock();
			BlockState blockState = block.getState();
			if(blockState instanceof Chest){
				Inventory inventory = ((Chest)blockState).getInventory();
				for(ItemStack itemStack : inventory){
					if(itemStack==null) continue;
					@SuppressWarnings("deprecation")
					String displayName = itemStack.getType().toString()+"-"+itemStack.getData().getData();
					int amount = itemStack.getAmount();
					if(!resources.containsKey(displayName)){
						resources.put(displayName, amount);
					}
					else{
						resources.put(displayName, resources.get(displayName)+amount);
					}
				}
			}
			else{
				continue;
			}
			List<String> arguments = new ArrayList<String>();
			arguments.add("addon="+addon_id);
			for(Entry<String,Integer> entry : resources.entrySet()){
				arguments.add("resources["+entry.getKey()+"]="+entry.getValue());
			}
			String[] argumentsArray = new String[arguments.size()];
			DataSource.getResponse("taxes/info.php", arguments.toArray(argumentsArray));
		}
	}
}

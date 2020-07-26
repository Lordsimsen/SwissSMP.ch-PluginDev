package ch.swisssmp.city;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CitySystemPlugin extends JavaPlugin{
	private static CitySystemPlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getPluginManager().registerEvents(new EventListener(), plugin);
		Bukkit.getPluginManager().registerEvents(new CraftingListener(), plugin);
		Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), plugin);
		Bukkit.getPluginCommand("cities").setExecutor(new CitiesCommand());
		Cities.loadAll();
		CraftingRecipes.register();
		ItemManager.updateItems();
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static String getPrefix(){
		return "["+ChatColor.RED+"Städtesystem"+ChatColor.RESET+"] ";
	}
	
	public static CitySystemPlugin getInstance(){
		return plugin;
	}
}

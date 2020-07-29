package ch.swisssmp.city;

import ch.swisssmp.city.guides.AddonEventListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class CitySystemPlugin extends JavaPlugin{
	private static CitySystemPlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		LivemapInterface.link();
		Bukkit.getPluginManager().registerEvents(new EventListener(), plugin);
		Bukkit.getPluginManager().registerEvents(new AddonEventListener(), plugin);
		Bukkit.getPluginManager().registerEvents(new CraftingListener(), plugin);
		Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), plugin);
		this.getCommand("cities").setExecutor(new CitiesCommand());
		this.getCommand("addon").setExecutor(new AddonCommand());
		this.getCommand("techtree").setExecutor(new AddonCommand());
		Techtrees.loadAll();
		Cities.loadAll();
		Citizenships.loadAll();
		Addons.loadAll();
		CraftingRecipes.register();
		ItemManager.updateItems();
		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
	}

	@Override
	public void onDisable() {
		Addons.unloadAll();
		Citizenships.unloadAll();
		Cities.unloadAll();
		Techtrees.unloadAll();
		Bukkit.getScheduler().cancelTasks(this);
		Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
	}
	
	public static String getPrefix(){
		return "["+ChatColor.RED+"Städtesystem"+ChatColor.RESET+"] ";
	}
	
	public static CitySystemPlugin getInstance(){
		return plugin;
	}
}

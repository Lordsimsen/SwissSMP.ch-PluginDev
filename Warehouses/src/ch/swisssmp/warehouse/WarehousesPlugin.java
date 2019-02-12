package ch.swisssmp.warehouse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class WarehousesPlugin extends JavaPlugin {
	private static WarehousesPlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		this.getCommand("warehousetool").setExecutor(new WarehouseToolCommand());
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		CraftingRecipes.register();
		SlaveCollections.loadAll();
		MasterCollections.loadAll();
		MasterChestsTriggerRoutine.start();
		SlaveChestsAnimationRoutine.start();
		
		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static WarehousesPlugin getInstance(){
		return plugin;
	}
	
	public static String getPrefix(){
		return "["+ChatColor.GOLD+"Lagerhaus"+ChatColor.RESET+"] ";
	}
}

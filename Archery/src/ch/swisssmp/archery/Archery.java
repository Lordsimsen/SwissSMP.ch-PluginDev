package ch.swisssmp.archery;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Archery extends JavaPlugin{
	private static PluginDescriptionFile pdfFile;
	private static Archery plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		ItemManager.registerRecipes();
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static Archery getInstance(){
		return plugin;
	}
}

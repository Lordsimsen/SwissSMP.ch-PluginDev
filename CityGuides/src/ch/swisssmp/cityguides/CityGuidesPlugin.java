package ch.swisssmp.cityguides;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CityGuidesPlugin extends JavaPlugin{
	private static CityGuidesPlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getPluginManager().registerEvents(new EventListener(), plugin);
		Bukkit.getPluginCommand("cityguide").setExecutor(new CityGuideCommand());
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
	
	public static CityGuidesPlugin getInstance(){
		return plugin;
	}
}
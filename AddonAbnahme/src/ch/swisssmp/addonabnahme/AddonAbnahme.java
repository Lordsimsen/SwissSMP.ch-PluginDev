package ch.swisssmp.addonabnahme;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.city.CitySystemPlugin;

public class AddonAbnahme extends JavaPlugin{
	private static AddonAbnahme plugin;
	
	@Override
	public void onEnable() {
		plugin = this;

		LivemapInterface.link();
		this.getCommand("addon").setExecutor(new AddonCommand());
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Techtrees.loadAll();
		
		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static AddonAbnahme getInstance(){
		return plugin;
	}
	
	public static String getPrefix(){
		return CitySystemPlugin.getPrefix();
	}
}

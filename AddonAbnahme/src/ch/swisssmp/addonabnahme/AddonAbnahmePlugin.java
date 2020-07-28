package ch.swisssmp.addonabnahme;

import ch.swisssmp.city.guides.AddonEventListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.city.CitySystemPlugin;

public class AddonAbnahmePlugin extends JavaPlugin{
	private static AddonAbnahmePlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		Bukkit.getPluginManager().registerEvents(new AddonEventListener(), this);
		
		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
	}
	
	public static AddonAbnahmePlugin getInstance(){
		return plugin;
	}
	
	public static String getPrefix(){
		return CitySystemPlugin.getPrefix();
	}
}

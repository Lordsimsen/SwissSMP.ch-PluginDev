package ch.swisssmp.customportals;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomPortalsPlugin extends JavaPlugin{
	protected static CustomPortalsPlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getPluginCommand("portals").setExecutor(new PortalsCommand());
		Bukkit.getPluginCommand("portal").setExecutor(new PortalCommand());
		CustomPortalContainers.loadAll();
		CustomPortalContainers.updateTokens();
		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
	}

	@Override
	public void onDisable() {
		CustomPortalContainers.unloadAll();
		HandlerList.unregisterAll(this);
		Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
	}

	public static CustomPortalsPlugin getInstance(){
		return plugin;
	}

	public static String getPrefix(){
		return "["+ ChatColor.LIGHT_PURPLE+plugin.getName()+ChatColor.RESET+"]";
	}
}

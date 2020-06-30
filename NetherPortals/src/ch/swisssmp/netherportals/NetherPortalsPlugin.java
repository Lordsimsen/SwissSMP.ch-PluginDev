package ch.swisssmp.netherportals;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class NetherPortalsPlugin extends JavaPlugin{
	private static NetherPortalsPlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getPluginCommand("netherportals").setExecutor(new NetherPortalsCommand());
		WorldConfigurations.loadAll();
		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
	}

	@Override
	public void onDisable() {
		WorldConfigurations.unloadAll();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PortalLinkCache.clear();
		Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
	}

	public static String getPrefix(){return "["+plugin.getName()+"]";}
	public static NetherPortalsPlugin getInstance(){
		return plugin;
	}
}

package ch.swisssmp.citymapdisplays;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.livemap_render_api.LivemapInfo;

public class CityMapDisplaysPlugin extends JavaPlugin {
	protected static CityMapDisplaysPlugin plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getPluginCommand("citymapdisplay").setExecutor(new PlayerCommand());
		Bukkit.getPluginCommand("citymapdisplays").setExecutor(new CityMapDisplaysCommand());
		CityMapDisplays.load();
		
		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
	}

	@Override
	public void onDisable() {
		CityMapDisplays.unload();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
	}
	
	public static CityMapDisplaysPlugin getInstance() {
		return plugin;
	}
	
	public static String getPrefix() {
		return "["+plugin.getName()+"]";
	}
	
	protected static LivemapInfo getLivemapInfo() {
		return new LivemapInfo("http://map.swisssmp.ch:8188/tiles/Varuna/t_day/", 30, BlockFace.SOUTH_EAST, 7, 16);
	}
}

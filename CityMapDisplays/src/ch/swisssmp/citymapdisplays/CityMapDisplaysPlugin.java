package ch.swisssmp.citymapdisplays;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.livemap_render_api.LivemapInfo;

public class CityMapDisplaysPlugin extends JavaPlugin {
	protected static PluginDescriptionFile pdfFile;
	protected static CityMapDisplaysPlugin plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getPluginCommand("citymapdisplay").setExecutor(new PlayerCommand());
		CityMapDisplays.load();
		
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		CityMapDisplays.unload();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
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

package ch.swisssmp.mapimageloader;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class MapImageLoaderPlugin extends JavaPlugin {
	protected static PluginDescriptionFile pdfFile;
	protected static MapImageLoaderPlugin plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		
		Bukkit.getPluginCommand("imageloader").setExecutor(new PlayerCommand());
		
		MapImages.load(new Ditherer(MapPalette.getVanillaMap()));
		MapViewCompositions.load();
		
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		MapImages.unload();
		MapViewCompositions.unload();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static MapImageLoaderPlugin getInstance() {
		return plugin;
	}
	
	public static String getPrefix() {
		return "["+plugin.getName()+"]";
	}
}

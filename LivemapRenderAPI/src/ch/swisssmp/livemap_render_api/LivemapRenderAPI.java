package ch.swisssmp.livemap_render_api;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class LivemapRenderAPI extends JavaPlugin {
	protected static PluginDescriptionFile pdfFile;
	protected static LivemapRenderAPI plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static LivemapRenderAPI getInstance() {
		return plugin;
	}
	
	public static String getPrefix() {
		return "["+plugin.getName()+"]";
	}
}

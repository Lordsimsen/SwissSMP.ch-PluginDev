package ch.swisssmp.livemap;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class LivemapMarkerAPI extends JavaPlugin {
	private static LivemapMarkerAPI plugin;
	
	@Override
	public void onEnable() {
		plugin = this;

		PluginDescriptionFile pdfFile = getDescription();
		Livemap.link();
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static LivemapMarkerAPI getInstance(){
		return plugin;
	}
}

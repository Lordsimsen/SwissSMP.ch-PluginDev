package ch.swisssmp.event;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.event.pluginlisteners.EventListenerMaster;

public class RemoteEventListener extends JavaPlugin{

	public static RemoteEventListener plugin;
	public static Logger logger;
	public static Server server;
	public static PluginDescriptionFile pdfFile;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		Bukkit.getPluginCommand("remoteeventlistener").setExecutor(new ConsoleCommand());
		EventListenerMaster.init();
	}
    
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}

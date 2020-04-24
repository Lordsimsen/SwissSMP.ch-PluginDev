package ch.swisssmp.permissionmanager;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionManagerPlugin extends JavaPlugin{
	private static Logger logger;
	private static PluginDescriptionFile pdfFile;
	private static PermissionManagerPlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		this.getCommand("permission").setExecutor(new PlayerCommand());
		this.getCommand("promote").setExecutor(new PlayerCommand());
		this.getCommand("demote").setExecutor(new PlayerCommand());
		PermissionManager.loadPermissions();
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static PermissionManagerPlugin getInstance(){
		return plugin;
	}
}

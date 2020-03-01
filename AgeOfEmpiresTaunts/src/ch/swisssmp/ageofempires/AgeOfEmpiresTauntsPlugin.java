package ch.swisssmp.ageofempires;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class AgeOfEmpiresTauntsPlugin extends JavaPlugin {

	private static PluginDescriptionFile pdfFile;
	private static AgeOfEmpiresTauntsPlugin plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static AgeOfEmpiresTauntsPlugin getInstance(){
		return plugin;
	}
}

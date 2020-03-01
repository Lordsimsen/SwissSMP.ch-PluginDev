package ch.swisssmp.lift;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class LiftPlugin extends JavaPlugin {
	private static LiftPlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		Debug.active = false;
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static LiftPlugin getInstance(){
		return plugin;
	}
	
	public static String getPrefix(){
		return "["+ChatColor.YELLOW+"Lift"+ChatColor.RESET+"] ";
	}
}

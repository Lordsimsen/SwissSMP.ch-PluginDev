package ch.swisssmp.blocks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomBlocksPlugin extends JavaPlugin {
	private static CustomBlocksPlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		
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
	
	public static CustomBlocksPlugin getInstance(){
		return plugin;
	}
	
	public static String getPrefix(){
		return ChatColor.RESET+"["+ChatColor.GRAY+plugin.getName()+ChatColor.RESET+"]";
	}
}

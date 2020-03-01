package ch.swisssmp.zones;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ZonesPlugin extends JavaPlugin {
	private static ZonesPlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		CraftingRecipes.register();
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getPluginCommand("zoneedit").setExecutor(new ZoneEditorCommand());
		
		for(World world : Bukkit.getWorlds()){
			ZoneContainer.load(world);
		}
		
		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
	}

	@Override
	public void onDisable() {
		ZoneContainers.clear();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static ZonesPlugin getInstance(){
		return plugin;
	}
	
	public static String getPrefix(){
		return "["+ChatColor.LIGHT_PURPLE+"Zonen"+ChatColor.RESET+"] ";
	}
}

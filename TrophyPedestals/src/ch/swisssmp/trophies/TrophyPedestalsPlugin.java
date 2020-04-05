package ch.swisssmp.trophies;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class TrophyPedestalsPlugin extends JavaPlugin {
	private static TrophyPedestalsPlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		CustomItemController.registerRecipe();
		
		for(World world : Bukkit.getWorlds()) {
			for(Chunk chunk : world.getLoadedChunks()) {
				EntityUtility.releaseDroppedItems(chunk);
			}
		}
		
		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		CustomItemController.unregisterRecipe();
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static TrophyPedestalsPlugin getInstance(){
		return plugin;
	}
	
	public static String getPrefix(){
		return ChatColor.RESET+"["+ChatColor.LIGHT_PURPLE+plugin.getName()+ChatColor.RESET+"]";
	}
}

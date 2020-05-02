package ch.swisssmp.zvierigame;


import ch.swisssmp.zvierigame.game.StopGameCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class ZvieriGamePlugin extends JavaPlugin{
	
	private static ZvieriGamePlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;	
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), plugin);
		
		Bukkit.getPluginCommand("zvieriarena").setExecutor(new ZvieriArenaCommand());
		Bukkit.getPluginCommand("zvieriarenen").setExecutor(new ZvieriArenenCommand());
		Bukkit.getPluginCommand("zvierigame").setExecutor(new StopGameCommand());
		
		for (World world : Bukkit.getWorlds()) {
			ZvieriArenen.load(world);
		}
		
	}
	
	@Override
	public void onDisable() {

		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);		
	}
	
	public static String getPrefix() {
		return "[" + ChatColor.GOLD + "ZvieriGame" + ChatColor.RESET + "]";
	}
	
	public static ZvieriGamePlugin getInstance() {
		return plugin;
	}

}

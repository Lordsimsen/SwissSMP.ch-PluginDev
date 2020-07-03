package ch.swisssmp.event.quarantine;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.event.quarantine.commands.QuarantineArenaCommand;
import ch.swisssmp.event.quarantine.commands.QuarantineArenasCommand;
import ch.swisssmp.event.quarantine.commands.QuarantineCommand;

public class QuarantineEventPlugin extends JavaPlugin {

	private static QuarantineEventPlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		Bukkit.getPluginCommand("qarenas").setExecutor(new QuarantineArenasCommand());
		Bukkit.getPluginCommand("qarena").setExecutor(new QuarantineArenaCommand());
		Bukkit.getPluginCommand("quarantine").setExecutor(new QuarantineCommand());
		
		for(World world : Bukkit.getWorlds()) {
			ArenaContainers.load(world);
		}
		
		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
	}

	@Override
	public void onDisable() {
		ArenaContainers.clear();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
	}
	
	public static QuarantineEventPlugin getInstance(){
		return plugin;
	}
	
	public static String getPrefix() {
		return "["+plugin.getName()+"]";
	}
}

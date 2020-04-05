package ch.swisssmp.event.quarantine;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.event.quarantine.commands.QuarantineArenaCommand;
import ch.swisssmp.event.quarantine.commands.QuarantineArenasCommand;
import ch.swisssmp.event.quarantine.commands.QuarantineCommand;

public class QuarantineEventPlugin extends JavaPlugin {

	private static PluginDescriptionFile pdfFile;
	private static QuarantineEventPlugin plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		Bukkit.getPluginCommand("qarenas").setExecutor(new QuarantineArenasCommand());
		Bukkit.getPluginCommand("qarena").setExecutor(new QuarantineArenaCommand());
		Bukkit.getPluginCommand("quarantine").setExecutor(new QuarantineCommand());
		
		for(World world : Bukkit.getWorlds()) {
			ArenaContainers.load(world);
		}
		
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		ArenaContainers.clear();
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static QuarantineEventPlugin getInstance(){
		return plugin;
	}
	
	public static String getPrefix() {
		return "["+plugin.getName()+"]";
	}
}

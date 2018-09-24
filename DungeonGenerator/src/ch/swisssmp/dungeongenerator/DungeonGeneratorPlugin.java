package ch.swisssmp.dungeongenerator;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class DungeonGeneratorPlugin extends JavaPlugin{
	private static PluginDescriptionFile pdfFile;
	private static DungeonGeneratorPlugin plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		Bukkit.getPluginCommand("generator").setExecutor(new GeneratorCommand());
		Bukkit.getPluginCommand("generators").setExecutor(new GeneratorsCommand());
		Bukkit.getPluginManager().registerEvents(new EventListener(), DungeonGeneratorPlugin.plugin);
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static DungeonGeneratorPlugin getInstance(){
		return plugin;
	}
}

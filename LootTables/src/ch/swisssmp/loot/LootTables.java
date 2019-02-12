package ch.swisssmp.loot;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class LootTables extends JavaPlugin{
	private static PluginDescriptionFile pdfFile;
	private static LootTables plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		Bukkit.getPluginCommand("loot").setExecutor(new LootCommand());
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static LootTables getInstance(){
		return plugin;
	}
}

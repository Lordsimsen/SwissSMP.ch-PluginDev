package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class UtilityCommands extends JavaPlugin{
	private static PluginDescriptionFile pdfFile;
	private static UtilityCommands plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();

		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("help").setExecutor(playerCommand);
		this.getCommand("seen").setExecutor(playerCommand);
		this.getCommand("list").setExecutor(playerCommand);
		this.getCommand("more").setExecutor(playerCommand);
		this.getCommand("amount").setExecutor(playerCommand);
		this.getCommand("hauptstadt").setExecutor(playerCommand);
		this.getCommand("choose").setExecutor(playerCommand);
		this.getCommand("rename").setExecutor(new RenameCommand());
		
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static UtilityCommands getInstance(){
		return plugin;
	}
}

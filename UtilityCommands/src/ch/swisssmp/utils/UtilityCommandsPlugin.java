package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class UtilityCommandsPlugin extends JavaPlugin{
	private static PluginDescriptionFile pdfFile;
	private static UtilityCommandsPlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();

		Bukkit.getPluginManager().registerEvents(new TeleportListener(), this);

		this.getCommand("help").setExecutor(new HelpCommand());
		this.getCommand("seen").setExecutor(new SeenCommand());
		this.getCommand("list").setExecutor(new ListCommand());
		this.getCommand("more").setExecutor(new MoreCommand());
		this.getCommand("amount").setExecutor(new AmountCommand());
		this.getCommand("hauptstadt").setExecutor(new HauptstadtCommand());
		this.getCommand("choose").setExecutor(new ChooseCommand());
		this.getCommand("gravity").setExecutor(new GravityCommand());
		this.getCommand("rename").setExecutor(new RenameCommand());
		this.getCommand("stall").setExecutor(new StallCommand());
		this.getCommand("heal").setExecutor(new HealCommand());

		this.getCommand("clear").setExecutor(new ClearCommand());

		this.getCommand("gamemode").setExecutor(new GamemodeCommand());
		this.getCommand("home").setExecutor(new HomeCommand());
		this.getCommand("back").setExecutor(new BackCommand());

		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static UtilityCommandsPlugin getInstance(){
		return plugin;
	}
}

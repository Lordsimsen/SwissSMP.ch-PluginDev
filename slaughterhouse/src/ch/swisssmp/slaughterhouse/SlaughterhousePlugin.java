package ch.swisssmp.slaughterhouse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class contains the initialization of the plugin.
 *
 * @author Plexon21
 *
 */
public class SlaughterhousePlugin extends JavaPlugin {

	private static JavaPlugin plugin;

	/**
	 * Enable the plugin on serverstart or after reload
	 */
	@Override
	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		FileConfiguration config = getConfig();
		
		getServer().getPluginManager().registerEvents(new SlaughterhouseListener(config), this);

		Bukkit.getLogger().info(getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		Bukkit.getLogger().info(getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
	}

	public static JavaPlugin getInstance(){
		return plugin;
	}

	public static String getPrefix(){
		return ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Schlachthaus" + ChatColor.WHITE + "]";
	}
}

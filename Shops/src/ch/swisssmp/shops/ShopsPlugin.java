package ch.swisssmp.shops;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopsPlugin extends JavaPlugin{
	protected PluginDescriptionFile pdfFile;
	protected static ShopsPlugin plugin;
	protected boolean debug;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		
		this.getCommand("shop").setExecutor(new ShopCommand());
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static String getPrefix(){
		return "["+ChatColor.YELLOW+"Shops"+ChatColor.RESET+"] ";
	}
	
	public static ShopsPlugin getInstance(){
		return plugin;
	}
}

package ch.swisssmp.events.halloween;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.customitems.CustomItems;

public class HalloweenEventPlugin extends JavaPlugin{
	private static PluginDescriptionFile pdfFile;
	private static HalloweenEventPlugin plugin;
	private ItemStack spookyDiscStack;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		spookyDiscStack = CustomItems.getCustomItemBuilder("SPOOKY_DISC").build();
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static ItemStack getSpookyDisc(){
		return plugin.spookyDiscStack.clone();
	}
	
	public static HalloweenEventPlugin getInstance(){
		return plugin;
	}
}

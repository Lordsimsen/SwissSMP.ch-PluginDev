package ch.swisssmp.stalker;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.stalker.listeners.EventListeners;

public class Stalker extends JavaPlugin{
	private static PluginDescriptionFile pdfFile;
	private static Stalker plugin;

	protected static HashMap<UUID, Search> searches = new HashMap<UUID, Search>();
	protected static boolean debug = false;
	
	private DataLink dataLink;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();

		EventListeners.registerAll();
		
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("search").setExecutor(playerCommand);
		dataLink = new DataLink();
		dataLink.start();
		
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		dataLink.interrupt();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static void log(LogEntry logEntry){
		plugin.dataLink.push(logEntry);
	}
	
	public static String getIdentifier(Entity entity){
		if(entity==null) return "Environment";
		if(entity instanceof Player) return ((Player)entity).getUniqueId().toString();
		return entity.getCustomName()!=null ? entity.getCustomName() : entity.getName();
	}
	
	public static String getIdentifier(InventoryHolder holder){
		if(holder==null) return "Unkown";
		if(holder instanceof Container){
			return ((Container)holder).getBlock().getType().toString();
		}
		else if(holder instanceof Entity){
			return Stalker.getIdentifier((Entity)holder);
		}
		else{
			return holder.getInventory().getName();
		}
	}
	
	public static Block getBlock(InventoryHolder holder){
		if(holder==null) return null;
		if(holder instanceof Container){
			return ((Container)holder).getBlock();
		}
		else if(holder instanceof Entity){
			return ((Entity)holder).getLocation().getBlock();
		}
		else{
			return null;
		}
	}
	
	public static Stalker getInstance(){
		return plugin;
	}
}

package ch.swisssmp.resourcepack;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ResourcepackManager extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static ResourcepackManager plugin;
	
	protected static HashMap<Player,String> playerMap = new HashMap<Player,String>();
	
	private ResourcepackUpdater resourcepackUpdater;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("resourcepack").setExecutor(playerCommand);
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		if(Bukkit.getPluginManager().getPlugin("AdventureDungeons")!=null){
			this.resourcepackUpdater = new AdventureResourcepackUpdater();
		}
		else{
			this.resourcepackUpdater = new VanillaResourcepackUpdater();
		}
	}
	
	public static void setResourcepack(Player player, String resourcepack){
		if(player==null) return;
		if(playerMap.containsKey(player) && playerMap.get(player).equals(resourcepack))
			return;
		playerMap.remove(player);
		playerMap.put(player, resourcepack);
		player.setResourcePack(resourcepack);
	}
	
	public static String getResourcepack(Player player){
		return playerMap.get(player);
	}
	
	public static void updateResourcepack(Player player, long delay){
		Bukkit.getScheduler().runTaskLater(ResourcepackManager.plugin, new Runnable(){
			public void run(){
				updateResourcepack(player);
			}
		}, delay);
	}
	
	public static void updateResourcepack(Player player){
		plugin.resourcepackUpdater.updateResourcepack(player);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}

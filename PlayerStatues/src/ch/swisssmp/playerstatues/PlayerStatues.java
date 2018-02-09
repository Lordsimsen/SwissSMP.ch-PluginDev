package ch.swisssmp.playerstatues;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerStatues extends JavaPlugin implements Listener{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static PlayerStatues plugin;
	protected static boolean debug;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		Bukkit.getPluginManager().registerEvents(this, this);
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("statue").setExecutor(playerCommand);
		StatueTypes.registerEntities();
	}

	@Override
	public void onDisable() {
		StatueTypes.unregisterEntities();
		HandlerList.unregisterAll((JavaPlugin)this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	@EventHandler
	private void onWorldLoad(WorldLoadEvent event){
		
	}
}

package ch.swisssmp.utils;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class SwissSMPUtils extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static SwissSMPUtils plugin;
	protected static boolean debug;
	
	protected static HashMap<UUID,UUID> replyMap = new HashMap<UUID,UUID>();
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");

		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("balance").setExecutor(playerCommand);
		this.getCommand("seen").setExecutor(playerCommand);
		this.getCommand("afk").setExecutor(playerCommand);
		this.getCommand("list").setExecutor(playerCommand);
		this.getCommand("worlds").setExecutor(playerCommand);
		this.getCommand("hauptstadt").setExecutor(playerCommand);
		
		//checkAfk();
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static void broadcastMessage(String message){
		for(Player player : Bukkit.getOnlinePlayers()){
			player.sendMessage(message);
		}
	}
}

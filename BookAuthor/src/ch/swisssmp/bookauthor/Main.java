package ch.swisssmp.bookauthor;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	private Logger logger;
	
	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("bookauthor").setExecutor(playerCommand);
		this.getCommand("booktitle").setExecutor(playerCommand);
		this.getCommand("booktype").setExecutor(playerCommand);
	}
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}

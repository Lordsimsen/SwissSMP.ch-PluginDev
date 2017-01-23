package ch.swisssmp.utils;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class SwissSMPUtils extends JavaPlugin{
	protected static Logger logger;
	protected static File configFile;
	protected static YamlConfiguration config;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static SwissSMPUtils plugin;
	protected static boolean debug;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}

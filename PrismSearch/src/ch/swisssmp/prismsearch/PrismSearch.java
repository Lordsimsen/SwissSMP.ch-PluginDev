package ch.swisssmp.prismsearch;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PrismSearch extends JavaPlugin{
	protected static JavaPlugin plugin;
	private Logger logger;
	protected YamlConfiguration config;
	protected static HashMap<UUID, Search> searches = new HashMap<UUID, Search>();

	@Override
	public void onEnable() {
		plugin = this;
		PluginDescriptionFile pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("search").setExecutor(playerCommand);
	}
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}

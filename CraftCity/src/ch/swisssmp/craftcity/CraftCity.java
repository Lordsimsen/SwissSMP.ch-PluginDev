package ch.swisssmp.craftcity;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class CraftCity extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static CraftCity plugin;
	protected static boolean debug;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		registerRecipes();
	}

	@Override
	public void onDisable() {
		unregisterRecipes();
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	protected static void registerRecipes(){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("cities/recipes.php");
		if(yamlConfiguration==null) return;
		
	}
	
	protected static void unregisterRecipes(){
		
	}
}

package ch.swisssmp.countdown;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class CountdownClockPlugin extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static CountdownClockPlugin plugin;
	
	private ConfigurationSection numberTemplates;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getPluginCommand("countdown").setExecutor(new CountdownCommand());
		CountdownClockPlugin.loadNumberTemplates();
		CountdownClock.loadAll();
		
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	protected static boolean[] getNumberTemplate(long number){
		List<Integer> numberTemplate = plugin.numberTemplates.getIntegerList("number_"+number);
		boolean[] result = new boolean[21];
		for(int i = 0; i < numberTemplate.size(); i++){
			result[i] = numberTemplate.get(i)==1;
		}
		return result;
	}
	
	private static void loadNumberTemplates(){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("clock/get_number_templates.php");
		if(yamlConfiguration==null || !yamlConfiguration.contains("numbers")) return;
		plugin.numberTemplates = yamlConfiguration.getConfigurationSection("numbers");
	}
	
	protected static void reload(){
		CountdownClockPlugin.loadNumberTemplates();
		CountdownClock.loadAll();
	}
}

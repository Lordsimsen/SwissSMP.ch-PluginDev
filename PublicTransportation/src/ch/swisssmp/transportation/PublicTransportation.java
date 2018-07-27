package ch.swisssmp.transportation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class PublicTransportation extends JavaPlugin{
	protected static PublicTransportation plugin;
	
	protected static Map<World,List<String>> trainStations = new HashMap<World,List<String>>();
	
	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		
		plugin = this;

		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getPluginCommand("transportation").setExecutor(new PlayerCommand());
		
		updateTrainStations();

		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	protected static void updateTrainStations(){
		for(World world : Bukkit.getWorlds()){
			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("transportation/stations.php", new String[]{
					"world="+URLEncoder.encode(world.getName())
			});
			if(yamlConfiguration==null) continue;
			if(!yamlConfiguration.contains("stations")) continue;
			trainStations.remove(world);
			trainStations.put(world, yamlConfiguration.getStringList("stations"));
		}
	}
	
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}

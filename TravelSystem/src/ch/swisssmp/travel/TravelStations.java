package ch.swisssmp.travel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.World;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;

public class TravelStations {
	
	private static HashMap<UUID,TravelStation> stations = new HashMap<UUID,TravelStation>();

	public static Collection<TravelStation> getAll(){
		return stations.values();
	}
	
	public static Collection<TravelStation> get(World world){
		List<TravelStation> result = new ArrayList<TravelStation>();
		for(TravelStation station : stations.values()){
			if(station.getWorld()!=world) continue;
			result.add(station);
		}
		return result;
	}
	
	public static void save(World world){
		File pluginDirectory = new File(world.getWorldFolder(), "plugindata/TravelSystem");
		File dataFile = new File(pluginDirectory, "stations.yml");
		
		if(!pluginDirectory.exists()) pluginDirectory.mkdirs();
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		ConfigurationSection stationsSection = yamlConfiguration.createSection("stations");
		int index = 0;
		for(TravelStation station : TravelStations.get(world)){
			ConfigurationSection stationSection = stationsSection.createSection("station_"+index);
			station.save(stationSection);
			index++;
		}
		yamlConfiguration.save(dataFile);
	}
	
	public static void load(World world){
		File dataFile = new File(world.getWorldFolder(), "plugindata/TravelSystem/stations.yml");
		if(dataFile.exists()){
			TravelStations.load(world, dataFile);
		}
	}
	
	private static void load(World world, File dataFile){
		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(dataFile);
		if(yamlConfiguration.contains("stations")){
			ConfigurationSection stationsSection = yamlConfiguration.getConfigurationSection("stations");
			for(String key : stationsSection.getKeys(false)){
				ConfigurationSection stationSection = stationsSection.getConfigurationSection(key);
				TravelStation station = TravelStation.load(world, stationSection);
				stations.put(station.getId(), station);
			}
		}
	}
	
	public static void unload(World world){
		Collection<TravelStation> stations = new ArrayList<TravelStation>();
		for(TravelStation station : TravelStations.get(world)){
			if(station.getWorld()!=world) continue;
			stations.add(station);
		}
		for(TravelStation station : stations){
			station.unload();
		}
	}
	
	protected static boolean containsKey(UUID station_id){
		return stations.containsKey(station_id);
	}
	
	protected static TravelStation get(UUID station_id){
		return stations.get(station_id);
	}
	
	protected static void remove(UUID station_id){
		stations.remove(station_id);
	}
	
	protected static void put(UUID station_id, TravelStation station){
		stations.put(station_id, station);
	}
}

package ch.swisssmp.city;

import java.util.Collection;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;

public class Cities {
	private static HashMap<Integer,City> cities = new HashMap<Integer,City>();
	
	protected static void add(City city){
		cities.put(city.getId(),city);
	}
	
	protected static City getCity(String key){
		if(!key.isEmpty() && StringUtils.isNumeric(key)){
			int city_id = Integer.parseInt(key);
			return cities.get(city_id);
		}
		for(City city : cities.values()){
			if(!city.getName().toLowerCase().contains(key.toLowerCase())) continue;
			return city;
		}
		return null;
	}
	
	protected static void remove(String key){
		City city = getCity(key);
		if(city==null) return;
		cities.remove(city.getId());
	}
	
	public static void load(){
		HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), "load_cities.php", new String[]{
				"world="+URLEncoder.encode(Bukkit.getWorlds().get(0).getName())
		});
		request.onFinish(()->{
			load(request.getJsonResponse());
		});
	}
	
	private static void load(JsonObject json){
		if(json==null || !json.has("cities")) return;
		cities.clear();
		JsonArray citiesArray = json.getAsJsonArray("cities");
		for(JsonElement element : citiesArray){
			JsonObject citySection = element.getAsJsonObject();
			City.load(citySection);
		}
	}
	
	protected static City getCity(int city_id){
		return cities.get(city_id);
	}
	
	public static Collection<City> getAll(){
		return cities.values();
	}
}

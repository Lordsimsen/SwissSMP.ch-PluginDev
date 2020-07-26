package ch.swisssmp.city;

import java.util.*;

import com.google.gson.JsonObject;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class CitySystem {
	public static HTTPRequest createCity(String name, Player mayor, Collection<Player> founders, String ringType, Block origin, long time){
		List<String> founderNames = new ArrayList<String>();
		for(Player player : founders){
			founderNames.add("founders[]="+player.getUniqueId().toString());
		}
		HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), "create_city.php", new String[]{
				"name="+URLEncoder.encode(name),
				"mayor="+mayor.getUniqueId().toString(),
				"world="+URLEncoder.encode(origin.getWorld().getName()),
				"place[x]="+origin.getX(),
				"place[y]="+origin.getY(),
				"place[z]="+origin.getZ(),
				"time="+time,
				"ring="+URLEncoder.encode(ringType),
				String.join("&", founderNames)
		});
		return request;
	}

	public static Optional<City> findCity(String key){
		return Cities.findCity(key);
	}

	public static Optional<City> getCity(UUID uid){
		return Cities.getCity(uid);
	}

	public static Collection<City> getCities(){
		return Cities.getAll();
	}

	public static Optional<City> loadCity(JsonObject json){
		return Cities.load(json);
	}
}

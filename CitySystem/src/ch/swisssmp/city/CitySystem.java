package ch.swisssmp.city;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
}

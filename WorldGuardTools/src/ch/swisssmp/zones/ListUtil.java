package ch.swisssmp.zones;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;

public class ListUtil {
	
	public static void recalculateOrder(List<Location> locations){
		List<Location> order = new ArrayList<Location>();
		HashSet<Location> pending = new HashSet<Location>(locations);
		order.add(pending.iterator().next());
		pending.remove(0);
		while(pending.size()>0){
			Location current = order.get(order.size()-1);
			Location closest = pending.iterator().next();
			double closestDistance = closest.distanceSquared(current);
			for(Location location : pending){
				double distance = location.distanceSquared(current);
				if(distance>=closestDistance) continue;
				closest = location;
				closestDistance = distance;
			}
			pending.remove(closest);
			if(closestDistance>9){
				order.add(closest);
			}
		}
		locations.clear();
		locations.addAll(order);
	}
}

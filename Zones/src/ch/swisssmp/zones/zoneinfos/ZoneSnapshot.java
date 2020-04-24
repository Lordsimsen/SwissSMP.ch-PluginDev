package ch.swisssmp.zones.zoneinfos;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class ZoneSnapshot {
	private List<Location> locations;
	
	public ZoneSnapshot(List<Location> locations){
		this.locations = new ArrayList<Location>(locations);
	}
	
	public List<Location> getPoints(){
		return locations;
	}
}

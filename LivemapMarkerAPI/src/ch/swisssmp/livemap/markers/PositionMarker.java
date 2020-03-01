package ch.swisssmp.livemap.markers;

import org.bukkit.Location;

import ch.swisssmp.livemap.Livemap;

public class PositionMarker extends Marker {

	private String icon;
	private Location location;
	
	public PositionMarker(Location location, String group_id, String marker_id, String icon) {
		super(location.getWorld(), group_id, marker_id);
		this.location = location;
		this.icon = icon;
	}
	
	public void setIcon(String icon){
		this.icon = icon;
	}
	
	public String getIcon(){
		return icon;
	}
	
	public void setLocation(Location location){
		this.location = location;
	}
	
	public Location getLocation(){
		return location;
	}

	@Override
	public void save() {
		Livemap.saveMarker(this);
	}

}

package ch.swisssmp.livemap.markers;

import org.bukkit.World;

public abstract class Marker {
	private World world;
	private String group_id;
	private String marker_id;
	private String label;
	private String description;
	private boolean persistent = true;
	
	public Marker(World world, String group_id, String marker_id){
		this.world = world;
		this.group_id = group_id;
		this.marker_id = marker_id;
	}
	
	public World getWorld(){
		return world;
	}
	
	public String getGroup(){
		return group_id;
	}
	
	public String getId(){
		return marker_id;
	}
	
	public String getLabel(){
		return label;
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public boolean isPersistent(){
		return persistent;
	}
	
	public void setPersistent(boolean persistent){
		this.persistent = persistent;
	}
	
	public abstract void save();
}

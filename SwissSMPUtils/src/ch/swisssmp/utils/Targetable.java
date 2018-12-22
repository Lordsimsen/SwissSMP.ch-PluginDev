package ch.swisssmp.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Targetable {
	private Entity entity;
	private Location location;
	
	public Targetable(Entity entity){
		this.entity = entity;
	}
	public Targetable(Location location){
		this.location = location;
	}
	
	public Location getLocation(){
		if(entity!=null) return this.entity instanceof Player ? ((Player)this.entity).getEyeLocation().add(0, -0.5, 0) : this.entity.getLocation();
		return this.location;
	}
}

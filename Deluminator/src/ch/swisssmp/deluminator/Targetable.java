package ch.swisssmp.deluminator;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Targetable {
	private Entity entity;
	private Location location;
	
	protected Targetable(Entity entity){
		this.entity = entity;
	}
	protected Targetable(Location location){
		this.location = location;
	}
	
	protected Location getLocation(){
		if(entity!=null) return this.entity instanceof Player ? ((Player)this.entity).getEyeLocation().add(0, -0.5, 0) : this.entity.getLocation();
		return this.location;
	}
}

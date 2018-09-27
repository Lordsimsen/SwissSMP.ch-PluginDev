package ch.swisssmp.adventuredungeons;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class RespawnPoint {
	private static HashMap<Entity,RespawnPoint> respawnPoints = new HashMap<Entity,RespawnPoint>();
	
	private final Entity location;
	
	private boolean active;
	
	private RespawnPoint(Entity location){
		this.location = location;
	}
	
	protected static RespawnPoint spawn(Location location){
		ArmorStand armorStand = (ArmorStand)location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		armorStand.setSmall(true);
		armorStand.setInvulnerable(true);
		armorStand.setVisible(false);
		RespawnPoint result = new RespawnPoint(armorStand);
		respawnPoints.put(armorStand,result);
		return result;
	}
}

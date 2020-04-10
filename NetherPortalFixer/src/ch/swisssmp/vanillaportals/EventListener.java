package ch.swisssmp.vanillaportals;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.util.BlockVector;

public class EventListener implements Listener {
	
	private final double netherSizeRatio = 2;

	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event){
		if(event.getTo()==null) return;

		Location remapped = getToLocation(event.getFrom(), event.getTo());
		if(remapped==null) return;
		event.setTo(remapped);
		event.setCanCreatePortal(false);
		event.setSearchRadius(1);
		event.setCreationRadius(0);
	}
	
	/**
	 * Verhindert, dass Items oder Mobs vom Portal teleportiert werden
	 * @param event
	 */
	@EventHandler
	public void onPortalEnter(EntityPortalEvent event){
		if(event.getTo()==null) return;

		Location remapped = getToLocation(event.getFrom(), event.getTo());
		if(remapped==null) return;
		event.setTo(remapped);
		event.setSearchRadius(1);
	}
	
	private Location getToLocation(Location from, Location to) {
		World fromWorld = from.getWorld();
		World toWorld = to.getWorld();
		
		Location cached = PortalLinkCache.getCached(from);
		if(cached!=null) {
			return cached;
		}
		
		Location remappedLocation;
		boolean toWorldIsNether;
		if(fromWorld==Bukkit.getWorlds().get(0) && toWorld.getName().equals(fromWorld.getName()+"_nether")){
			/**
			 * Spieler betritt Nether
			 * Notiert informationen über den Eingangspunkt eines Reisenden
			 */
			Location rawLocation = from;
			remappedLocation = new Location(toWorld, rawLocation.getX() / netherSizeRatio, rawLocation.getY(), rawLocation.getZ() / netherSizeRatio, rawLocation.getYaw(), rawLocation.getPitch());
			toWorldIsNether = true;
		}
		else if(toWorld==Bukkit.getWorlds().get(0) && fromWorld.getName().equals(toWorld.getName()+"_nether")){
			/**
			 * Spieler verlässt Nether
			 * Stellt bei der Rückreise sicher, dass der Reisende zu seinem Eingangspunkt gesetzt wird
			 */
			Location rawLocation = from;
			remappedLocation = new Location(toWorld, rawLocation.getX() * netherSizeRatio, rawLocation.getY(), rawLocation.getZ() * netherSizeRatio);
			toWorldIsNether = false;
		}
		else {
			return null;
		}
		
		remappedLocation = NetherPortalAgent.getTargetLocation(remappedLocation, toWorldIsNether ? 64 : 128, 16, new BlockVector(4,4,3), new BlockVector(4,4,1), toWorldIsNether);
		remappedLocation.setYaw(from.getYaw());
		remappedLocation.setPitch(from.getPitch());
		
		PortalLinkCache.create(from, remappedLocation, 60*20); // 60s * 20tps
		return remappedLocation;
	}
}

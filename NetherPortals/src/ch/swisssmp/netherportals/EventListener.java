package ch.swisssmp.netherportals;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class EventListener implements Listener {

	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event){
		if(event.getCause()!= PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) return;
		WorldConfiguration configuration = WorldConfiguration.get(event.getFrom().getWorld());
		if(!configuration.isEnabled()){
			event.setCancelled(true);
			return;
		}
		if(!configuration.isSetupComplete()) return;
		Location remapped = configuration.createToLocation(event.getFrom());
		if(remapped==null) return;
		event.setTo(remapped);
		event.setCanCreatePortal(false);
		event.setSearchRadius(1);
		event.setCreationRadius(0);
	}

	@EventHandler
	public void onPortalEnter(EntityPortalEvent event){
		WorldConfiguration configuration = WorldConfiguration.get(event.getFrom().getWorld());
		if(!configuration.isEnabled()){
			event.setCancelled(true);
			return;
		}
		if(!configuration.isSetupComplete()) return;
		Location remapped = configuration.createToLocation(event.getFrom());
		if(remapped==null) return;
		event.setTo(remapped);
		event.setSearchRadius(1);
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event){
		WorldConfigurations.load(event.getWorld());
	}

	@EventHandler
	public void onWorldUnload(WorldUnloadEvent event){
		WorldConfigurations.unload(event.getWorld());
	}
}

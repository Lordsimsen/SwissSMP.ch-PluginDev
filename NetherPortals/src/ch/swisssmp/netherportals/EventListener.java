package ch.swisssmp.netherportals;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class EventListener implements Listener {

	private static final boolean DEBUG = false;

	private static void print(String text){
		if(!DEBUG) return;
		Bukkit.getLogger().info(NetherPortalsPlugin.getPrefix()+" "+text);
	}

	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event){
		print("PlayerPortalEvent");
		if(event.getCause()!= PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) return;
		WorldConfiguration configuration = WorldConfiguration.get(event.getFrom().getWorld());
		if(!configuration.isEnabled()){
			event.setCancelled(true);
			print("NetherPortals are disabled");
			return;
		}
		if(!configuration.isSetupComplete()){
			print("Setup incomplete");
			return;
		}
		Location remapped = configuration.createToLocation(event.getFrom());
		if(remapped==null){
			print("Remapped location is null");
			return;
		}
		event.setTo(remapped);
		event.setCanCreatePortal(false);
		event.setSearchRadius(8);
		event.setCreationRadius(0);
		print("Remapped location: "+remapped.getX()+","+remapped.getY()+","+remapped.getZ()+" ("+remapped.getWorld().getName()+")");
		// Bukkit.getScheduler().runTaskLater(NetherPortalsPlugin.getInstance(), ()->Bukkit.getLogger().info(event.isCancelled() ? "PortalTravel was cancelled" : "PortalTravel was not cancelled"), 1L);
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

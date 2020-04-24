package ch.swisssmp.adventuredungeons.event.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class EventListenerEntity extends EventListenerBasic{
	
	private List<Entity> primedMinecarts = new ArrayList<Entity>();
	
	public EventListenerEntity(EventListenerMaster master) {
		super(master);
	}
	@EventHandler
	private void onEntitySpawn(CreatureSpawnEvent event){
		if(event.getLocation().getWorld()!=this.getInstance().getWorld()) return;
		SpawnReason reason = event.getSpawnReason();
		if(reason == SpawnReason.NATURAL){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled=true)
	private void onFrameBreak(HangingBreakEvent event) {
		if(event.getEntity().getLocation().getWorld()!=this.getInstance().getWorld()) return;
	    if (event.getCause()==RemoveCause.ENTITY || event.getCause()==RemoveCause.EXPLOSION) {
	    	event.setCancelled(true);
	    }
	}
	
	@EventHandler
	private void onTNTMinecartIgnite(ExplosionPrimeEvent event){
		if(event.getEntityType()!=EntityType.MINECART_TNT) return;
		if(event.getEntity().getLocation().getWorld()!=this.getInstance().getWorld()) return;
		this.primedMinecarts.add(event.getEntity());
	}
	
	//only primed minecarts should explode, the other behaviour is not wanted here
	@EventHandler
	private void onTNTMinecartExplode(EntityExplodeEvent event){
		if(event.getEntityType()!=EntityType.MINECART_TNT) return;
		if(event.getEntity().getLocation().getWorld()!=this.getInstance().getWorld()) return;
		if(!this.primedMinecarts.contains(event.getEntity())){
			event.setCancelled(true);
			return;
		}
		this.primedMinecarts.remove(event.getEntity());
	}
}

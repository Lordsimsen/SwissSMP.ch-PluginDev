package ch.swisssmp.customentities;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EventListener implements Listener{
	@EventHandler
	private void onEntityDeath(EntityDeathEvent event){
		CustomEntity entity = CustomEntity.get(event.getEntity());
		if(entity!=null)entity.OnDeath();
	}
}

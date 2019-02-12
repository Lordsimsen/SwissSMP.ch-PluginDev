package ch.swisssmp.stalker.listeners.world;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;

import com.google.gson.JsonObject;

import ch.swisssmp.stalker.LogEntry;
import ch.swisssmp.stalker.Stalker;

public class WorldEventListener implements Listener {
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPortalCreate(PortalCreateEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry("Environment");
		logEntry.setWhat("PORTAL_CREATE");
		logEntry.setWhere(event.getBlocks().get(0));
		JsonObject extraData = new JsonObject();
		extraData.addProperty("reason", event.getReason().toString());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
}

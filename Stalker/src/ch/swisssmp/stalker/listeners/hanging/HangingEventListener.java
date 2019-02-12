package ch.swisssmp.stalker.listeners.hanging;

import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;

import com.google.gson.JsonObject;

import ch.swisssmp.stalker.LogEntry;
import ch.swisssmp.stalker.Stalker;
import ch.swisssmp.utils.SwissSMPUtils;

public class HangingEventListener implements Listener {
	@EventHandler(priority=EventPriority.MONITOR)
	private void onHangingBreakByEntity(HangingBreakByEntityEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getRemover());
		logEntry.setWhat("HANGING_BREAK_BY_ENTITY");
		logEntry.setWhere(event.getEntity().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		if(event.getEntity() instanceof ItemFrame){
			extraData.addProperty("item", SwissSMPUtils.encodeItemStack(((ItemFrame)event.getEntity()).getItem()));
		}
		extraData.addProperty("entity_type", event.getEntity().getType().toString());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onHangingBreak(HangingBreakEvent event){
		if(event instanceof HangingBreakByEntityEvent) return;
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getEntity());
		logEntry.setWhat("HANGING_BREAK_BY_ENTITY");
		logEntry.setWhere(event.getEntity().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		if(event.getEntity() instanceof ItemFrame){
			extraData.addProperty("item", SwissSMPUtils.encodeItemStack(((ItemFrame)event.getEntity()).getItem()));
		}
		extraData.addProperty("entity_type", event.getEntity().getType().toString());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onHangingPlace(HangingPlaceEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("HANGING_PLACE");
		logEntry.setWhere(event.getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("entity_type", event.getEntity().getType().toString());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
}

package ch.swisssmp.stalker.listeners.vehicle;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import com.google.gson.JsonObject;

import ch.swisssmp.stalker.LogEntry;
import ch.swisssmp.stalker.Stalker;

public class VehicleEventListener implements Listener {
	@EventHandler(priority=EventPriority.MONITOR)
	private void onVehicleCreate(VehicleCreateEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getVehicle());
		logEntry.setWhat("VEHICLE_CREATE");
		logEntry.setWhere(event.getVehicle().getLocation().getBlock());
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onVehicleDestroy(VehicleDestroyEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getAttacker());
		logEntry.setWhat("VEHICLE_DESTROY");
		logEntry.setWhere(event.getVehicle().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		if(event.getVehicle().getCustomName()!=null) extraData.addProperty("name", event.getVehicle().getCustomName());
		extraData.addProperty("entity_type", event.getVehicle().getType().toString());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onVehicleEnter(VehicleEnterEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getEntered());
		logEntry.setWhat("VEHICLE_ENTER");
		logEntry.setWhere(event.getVehicle().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		if(event.getVehicle().getCustomName()!=null) extraData.addProperty("name", event.getVehicle().getCustomName());
		extraData.addProperty("entity_type", event.getVehicle().getType().toString());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onVehicleExit(VehicleExitEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getExited());
		logEntry.setWhat("VEHICLE_EXIT");
		logEntry.setWhere(event.getVehicle().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		if(event.getVehicle().getCustomName()!=null) extraData.addProperty("name", event.getVehicle().getCustomName());
		extraData.addProperty("entity_type", event.getVehicle().getType().toString());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
}

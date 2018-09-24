package ch.swisssmp.dungeongenerator;

import org.bukkit.util.BlockVector;

import com.google.gson.JsonObject;

public abstract class LogEntry {
	
	private final BlockVector gridPosition;
	private final DungeonFloor floor;
	
	protected LogEntry(BlockVector gridPosition, DungeonFloor floor){
		this.gridPosition = gridPosition;
		this.floor = floor;
	}
	
	protected JsonObject getLogData(){
		JsonObject result = new JsonObject();
		result.addProperty("type", this.getLogType());
		result.addProperty("floor", this.floor.getFloorIndex());
		result.addProperty("x", this.gridPosition.getBlockX());
		result.addProperty("y", this.gridPosition.getBlockY());
		result.addProperty("z", this.gridPosition.getBlockZ());
		return result;
	}
	
	protected abstract String getLogType();
}

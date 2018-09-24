package ch.swisssmp.dungeongenerator;

import java.util.Collection;

import org.bukkit.util.BlockVector;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class LogEntryCorridorLengthCalculation extends LogEntry {
	
	private final Collection<GenerationPart> corridor;
	private final GenerationPart closestToStart;
	private final int calculatedLength;
	
	protected LogEntryCorridorLengthCalculation(BlockVector gridPosition, DungeonFloor floor, Collection<GenerationPart> corridor, GenerationPart closestToStart, int calculatedLength) {
		super(gridPosition, floor);
		this.corridor = corridor;
		this.closestToStart = closestToStart;
		this.calculatedLength = calculatedLength;
	}
	
	@Override
	protected JsonObject getLogData(){
		JsonObject result = super.getLogData();
		JsonArray corridorData = new JsonArray();
		JsonObject corridorPartData;
		for(GenerationPart corridorPart : this.corridor){
			corridorPartData = new JsonObject();
			corridorPartData.addProperty("x", corridorPart.getGridPosition().getBlockX());
			corridorPartData.addProperty("y", corridorPart.getGridPosition().getBlockY());
			corridorPartData.addProperty("z", corridorPart.getGridPosition().getBlockZ());
			corridorPartData.addProperty("distance", corridorPart.getDistance(PartType.START));
			corridorData.add(corridorPartData);
		}
		result.add("corridor", corridorData);
		JsonObject closestData = new JsonObject();
		closestData.addProperty("x", closestToStart.getGridPosition().getBlockX());
		closestData.addProperty("y", closestToStart.getGridPosition().getBlockY());
		closestData.addProperty("z", closestToStart.getGridPosition().getBlockZ());
		closestData.addProperty("distance", closestToStart.getDistance(PartType.START));
		result.add("closest", closestData);
		result.addProperty("length", this.calculatedLength);
		return result;
	}

	@Override
	protected String getLogType() {
		return "CORRIDOR_LENGTH_CALC";
	}

}

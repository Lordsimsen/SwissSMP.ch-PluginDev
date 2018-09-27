package ch.swisssmp.dungeongenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.util.BlockVector;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class LogEntryPartSelection extends LogEntry{

	private final PartGenerationMode mode;
	private final Map<PartType,Integer> distances;
	private final Map<PartType,String> validPartTypes;
	private final Map<ProxyGeneratorPart,String> rejected;
	private final Collection<ProxyGeneratorPart> available;
	private final ProxyGeneratorPart selection;
	
	private int corridorLength = -1;
	
	private boolean detailedReport = false;
	
	protected LogEntryPartSelection(BlockVector gridPosition, DungeonFloor floor, PartGenerationMode mode, Map<PartType,Integer> distances, Map<PartType,String> validPartTypes, Map<ProxyGeneratorPart,String> rejected, Collection<ProxyGeneratorPart> available, ProxyGeneratorPart selection) {
		super(gridPosition, floor);
		this.mode = mode;
		this.distances = new HashMap<PartType,Integer>(distances);
		this.validPartTypes = validPartTypes;
		this.rejected = rejected;
		this.available = available;
		this.selection = selection;
	}
	
	protected void setCorridorLength(int corridorLength){
		this.corridorLength = corridorLength;
	}
	
	@Override
	protected JsonObject getLogData(){
		JsonObject result = super.getLogData();
		result.addProperty("mode", this.mode.toString());
		JsonObject distancesData = new JsonObject();
		for(Entry<PartType,Integer> entry : this.distances.entrySet()){
			distancesData.addProperty(entry.getKey().toString(), entry.getValue());
		}
		result.add("distances", distancesData);
		JsonObject typesInfo = new JsonObject();
		for(Entry<PartType,String> partType : this.validPartTypes.entrySet()){
			typesInfo.addProperty(partType.getKey().toString(), partType.getValue());
		}
		result.add("part_types", typesInfo);
		if(detailedReport){
			JsonArray rejectedArray = new JsonArray();
			JsonObject rejectedEntry;
			for(Entry<ProxyGeneratorPart,String> entry : rejected.entrySet()){
				rejectedEntry = new JsonObject();
				rejectedEntry.addProperty("original", entry.getKey().getId());
				rejectedEntry.addProperty("rotation", entry.getKey().getRotation());
				rejectedEntry.addProperty("reason", entry.getValue());
			}
			result.add("rejected", rejectedArray);
		}
		JsonArray availableArray = new JsonArray();
		JsonObject availableData;
		for(ProxyGeneratorPart part : available){
			availableData = new JsonObject();
			availableData.addProperty("original", part.getId());
			availableData.addProperty("rotation", part.getRotation());
			availableArray.add(availableData);
		}
		result.add("available", availableArray);
		if(this.selection!=null){
			JsonObject selectionData = new JsonObject();
			selectionData.addProperty("original", selection.getId());
			selectionData.addProperty("rotation", selection.getRotation());
			result.add("selection", selectionData);
		}
		if(this.corridorLength>=0){
			result.addProperty("corridor_length", this.corridorLength);
		}
		return result;
	}

	@Override
	protected String getLogType() {
		return "PART_SELECTION";
	}

}

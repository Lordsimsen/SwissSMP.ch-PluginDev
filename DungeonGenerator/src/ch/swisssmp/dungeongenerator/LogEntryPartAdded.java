package ch.swisssmp.dungeongenerator;

import com.google.gson.JsonObject;

public class LogEntryPartAdded extends LogEntry {

	private final GenerationPart part;
	
	protected LogEntryPartAdded(GenerationPart part) {
		super(part.getGridPosition(), part.getFloor());
		this.part = part;
	}
	
	@Override
	protected JsonObject getLogData() {
		JsonObject result = super.getLogData();
		result.addProperty("original", part.getProxy().getId());
		result.addProperty("rotation", part.getProxy().getRotation());
		if(part.getImage()!=null) result.addProperty("image", part.getImage());
		JsonObject distancesData = new JsonObject();
		for(PartType partType : PartType.values()){
			distancesData.addProperty(partType.toString(), part.getDistance(partType));
		}
		result.add("distances", distancesData);
		return result;
	}

	@Override
	protected String getLogType() {
		return "PART_ADDED";
	}

}

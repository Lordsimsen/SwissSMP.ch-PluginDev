package ch.swisssmp.dungeongenerator;

import com.google.gson.JsonObject;

public class LogEntryPartRemoved extends LogEntry {
	private final GenerationPart part;
	
	protected LogEntryPartRemoved(GenerationPart part) {
		super(part.getGridPosition(), part.getFloor());
		this.part = part;
	}
	
	@Override
	protected JsonObject getLogData() {
		JsonObject result = super.getLogData();
		result.addProperty("original", part.getProxy().getId());
		result.addProperty("rotation", part.getProxy().getRotation());
		return result;
	}

	@Override
	protected String getLogType() {
		return "PART_REMOVED";
	}
}

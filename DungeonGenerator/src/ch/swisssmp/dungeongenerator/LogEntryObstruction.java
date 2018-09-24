package ch.swisssmp.dungeongenerator;

public class LogEntryObstruction extends LogEntry {

	protected LogEntryObstruction(ObstructedGenerationPart part){
		super(part.getGridPosition(), part.getFloor());
	}

	@Override
	protected String getLogType() {
		return "OBSTRUCTION";
	}

}

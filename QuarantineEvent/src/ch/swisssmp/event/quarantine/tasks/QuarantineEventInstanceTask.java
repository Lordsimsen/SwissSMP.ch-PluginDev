package ch.swisssmp.event.quarantine.tasks;

import ch.swisssmp.event.quarantine.QuarantineArena;
import ch.swisssmp.event.quarantine.QuarantineEventInstance;

public abstract class QuarantineEventInstanceTask extends Task {
	
	private final QuarantineEventInstance instance;
	private final QuarantineArena arena;
	
	protected QuarantineEventInstanceTask(QuarantineEventInstance instance) {
		this.instance = instance;
		this.arena = instance.getArena();
	}
	
	public QuarantineEventInstance getInstance() {
		return instance;
	}
	
	public QuarantineArena getArena() {
		return arena;
	}
}

package ch.swisssmp.event.quarantine.tasks;

import ch.swisssmp.event.quarantine.QuarantineEventInstance;
import ch.swisssmp.event.quarantine.QuarantineEventInstance.Phase;

/**
 * @author Oliver
 *
 */
public class InitializePhase extends QuarantineEventInstanceTask {

	public InitializePhase(QuarantineEventInstance instance) {
		super(instance);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onInitialize() {
		getInstance().findSpawners();
	}
	
	@Override
	public void run() {
		complete();
	}
	
	@Override
	protected void onComplete() {
		getInstance().setPhase(Phase.Preparation);
	}
}

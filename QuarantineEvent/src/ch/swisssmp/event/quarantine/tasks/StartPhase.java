package ch.swisssmp.event.quarantine.tasks;

import java.util.UUID;

import org.bukkit.ChatColor;

import ch.swisssmp.event.quarantine.QuarantineEventInstance;
import ch.swisssmp.event.quarantine.QuarantineEventInstance.Phase;
import ch.swisssmp.utils.SwissSMPler;

/**
 * Führt Startsequenz aus
 * @author Oliver
 *
 */
public class StartPhase extends QuarantineEventInstanceTask {

	public StartPhase(QuarantineEventInstance instance) {
		super(instance);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		QuarantineEventInstance instance = getInstance();
		for(UUID playerUid : getInstance().getPlayers()) {
			String description;
			String message;
			if(instance.isInfected(playerUid)) {
				description = ChatColor.RED+"Du bist infiziert!";
				message = ChatColor.LIGHT_PURPLE+"Jage andere Spieler und infiziere sie, indem du sie attackierst.";
			}
			else {
				description = ChatColor.GREEN+"Kämpfe um dein Überleben!";
				message = ChatColor.GREEN+"Flüchte vor den Infizierten und suche ein Heilmittel.";
			}
			SwissSMPler swissSMPler = SwissSMPler.get(playerUid);
			swissSMPler.sendTitle("Los gehts!", description);
			swissSMPler.sendMessage(message);
		}
		complete();
	}

	
	@Override
	protected void onComplete() {
		getInstance().setPhase(Phase.Play);
	}
}

package ch.swisssmp.flyday;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ch.swisssmp.lift.event.LiftExitEvent;

public class LiftHandler implements Listener {
	@EventHandler
	private void onLiftExit(LiftExitEvent event){
		if(event.getEntityType()!=EntityType.PLAYER) return;
		Player player = (Player) event.getEntity();
		FlyDay.updatePlayer(player, UpdateFlag.QUIET);
	}
}

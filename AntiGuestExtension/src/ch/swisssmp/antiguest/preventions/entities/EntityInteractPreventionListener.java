package ch.swisssmp.antiguest.preventions.entities;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import ch.swisssmp.antiguest.preventions.Prevention;

public class EntityInteractPreventionListener implements Listener {
	
	private final EntityInteractPrevention[] preventions;
	
	public EntityInteractPreventionListener(EntityInteractPrevention[] preventions) {
		this.preventions = preventions;
	}

	@EventHandler
	private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if(event.getRightClicked()==null ||
				event.getPlayer().hasPermission("antiguest_extension.preventions.*") ||
				event.getRightClicked().isInvulnerable()) return;
		EntityType entityType = event.getRightClicked().getType();
		Player player = event.getPlayer();
		Prevention triggered = null;
		for(EntityInteractPrevention prevention : preventions) {
			if(entityType!=prevention.GetType() || player.hasPermission("antiguest_extension.preventions.entities."+prevention.GetSubPermission())) continue;
			triggered = prevention;
			break;
		}
		if(triggered==null) return;
		event.setCancelled(true);
		triggered.trigger((Player) event.getPlayer());
	}
}

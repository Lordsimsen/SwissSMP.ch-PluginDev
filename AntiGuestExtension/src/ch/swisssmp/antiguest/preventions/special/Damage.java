package ch.swisssmp.antiguest.preventions.special;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import ch.swisssmp.antiguest.preventions.Prevention;

public class Damage extends Prevention implements Listener {

	@EventHandler
	private void onPlayerDamageEntity(EntityDamageByEntityEvent event) {
		if(event.getDamager()==null || !(event.getDamager() instanceof Player)) return;
		Player player = (Player) event.getDamager();
		if(player.hasPermission("antiguest_extension.preventions.damage")) return;
		event.setCancelled(true);
		trigger(player);
	}
}

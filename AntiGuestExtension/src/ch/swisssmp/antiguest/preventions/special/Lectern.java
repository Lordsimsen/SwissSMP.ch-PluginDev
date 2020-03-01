package ch.swisssmp.antiguest.preventions.special;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;

import ch.swisssmp.antiguest.preventions.Prevention;

public class Lectern extends Prevention implements Listener {
	@EventHandler
	private void onPlayerOpenLectern(PlayerTakeLecternBookEvent event) {
		Player player = event.getPlayer();
		if(player.hasPermission("antiguest_extension.preventions.lectern")) return;
		event.setCancelled(true);
		trigger(player);
	}
}

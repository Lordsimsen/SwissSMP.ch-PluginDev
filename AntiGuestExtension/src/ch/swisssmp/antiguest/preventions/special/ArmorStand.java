package ch.swisssmp.antiguest.preventions.special;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

import ch.swisssmp.antiguest.preventions.Prevention;

public class ArmorStand extends Prevention implements Listener {

	@EventHandler
	private void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
		if(event.getPlayer().hasPermission("antiguest_extension.preventions.armor_stand")) return;
		event.setCancelled(true);
		trigger(event.getPlayer());
	}
}

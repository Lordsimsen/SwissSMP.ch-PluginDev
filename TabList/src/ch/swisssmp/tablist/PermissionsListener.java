package ch.swisssmp.tablist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ch.swisssmp.permissionmanager.PlayerPermissionsChangedEvent;

public class PermissionsListener implements Listener {
	
	@EventHandler
	private void onPlayerPermissionsChanged(PlayerPermissionsChangedEvent event){
		TabList.configurePlayer(event.getPlayer(), event.isJoining());
	}
}

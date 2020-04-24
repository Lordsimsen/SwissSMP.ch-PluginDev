package ch.swisssmp.permissionmanager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;

public class EventListener implements Listener {

	@EventHandler(ignoreCancelled=true)
	private void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		DataSource.getResponse(PermissionManagerPlugin.getInstance(), "login.php", new String[]{
				"player_uuid="+player.getUniqueId().toString(),
				"player_name="+URLEncoder.encode(player.getName()),
			});
		PermissionManager.loadPermissions(player, true);
	}
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		PermissionManager.remove(player.getUniqueId());
		DataSource.getResponse(PermissionManagerPlugin.getInstance(), "logout.php", new String[]{
				"player_uuid="+player.getUniqueId().toString(),
			});
	}
}

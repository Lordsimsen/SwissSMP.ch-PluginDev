package ch.swisssmp.permissionmanager;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PexCommandListener implements Listener{
	protected PexCommandListener(){
		Bukkit.getPluginManager().registerEvents(this, PermissionManager.plugin);
	}
	@EventHandler
	private void onPlayerCommand(PlayerCommandPreprocessEvent event){
		String message = event.getMessage().toLowerCase();
		if(message.startsWith("/pex ")){
			event.setMessage(message.replace("/pex ", "/permission "));
		}
		else if(message.startsWith("/promote")){
			event.setMessage(message.replace("/promote ", "/permission promote "));
		}
		else if(message.startsWith("/demote")){
			event.setMessage(message.replace("/promote ", "/permission demote "));
		}
	}
}

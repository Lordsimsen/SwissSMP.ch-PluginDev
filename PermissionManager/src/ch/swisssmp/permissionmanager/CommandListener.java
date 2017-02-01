package ch.swisssmp.permissionmanager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

public class CommandListener implements Listener{
	protected CommandListener(){
		Bukkit.getPluginManager().registerEvents(this, PermissionManager.plugin);
	}
	@EventHandler
	private void onPlayerTabComplete(TabCompleteEvent event){
		CommandSender sender = event.getSender();
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		String chatMessage = event.getBuffer();
		if(chatMessage.trim().charAt(0) == '/'){
			if(!chatMessage.contains(" ") && !player.hasPermission("permissionmanager.commands.autocomplete")){
				event.getCompletions().clear();
			}
		}
	}
}

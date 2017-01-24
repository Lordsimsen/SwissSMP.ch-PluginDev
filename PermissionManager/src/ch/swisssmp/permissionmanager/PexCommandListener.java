package ch.swisssmp.permissionmanager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

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
			event.setMessage(message.replace("/demote ", "/permission demote "));
		}
	}
	@EventHandler
	private void onPlayerTabComplete(TabCompleteEvent event){
		Bukkit.getLogger().info("PlayerChatTabCompleteEvent called");
		CommandSender sender = event.getSender();
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		String chatMessage = event.getBuffer();
		if(chatMessage.trim().charAt(0) == '/'){
			Bukkit.getLogger().info("String starts with /");
			if(!chatMessage.contains(" ") && !player.hasPermission("permissionmanager.commands.autocomplete")){
				Bukkit.getLogger().info("Prevented command autocomplete");
				event.getCompletions().clear();
			}
			else{
				Bukkit.getLogger().info("Auto Complete granted");
			}
		}
	}
}

package ch.swisssmp.resourcepack;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "set":{
			if(args.length<3) return false;
			String playerString = args[1];
			Player player = Bukkit.getPlayer(playerString);
			if(player==null){
				player = Bukkit.getPlayer(UUID.fromString(playerString));
			}
			if(player==null){
				sender.sendMessage("[§1ResourcepackManager§r] §cSpieler "+playerString+" nicht gefunden.");
				return true;
			}
			String resourcepack = args[2];
			ResourcepackManager.setResourcepack(player, resourcepack);
			break;
		}
		case "get":{
			if(args.length<2) return false;
			String playerString = args[1];
			Player player = Bukkit.getPlayer(playerString);
			if(player==null){
				player = Bukkit.getPlayer(UUID.fromString(playerString));
			}
			if(player==null){
				sender.sendMessage("[§1ResourcepackManager§r] §cSpieler "+playerString+" nicht gefunden.");
				return true;
			}
			if(!ResourcepackManager.playerMap.containsKey(player)){
				sender.sendMessage("[§1ResourcepackManager§r] §7"+player.getName()+" hat aktuell kein Server-Resourcepack aktiv.");
				return true;
			}
			else{
				String resourcepack = ResourcepackManager.getResourcepack(player);
				sender.sendMessage("[§1ResourcepackManager§r] §7"+player.getName()+" hat folgendes Server-Resourcepack aktiv: ");
				sender.sendMessage(resourcepack);
			}
			break;
		}
		case "reload":{
			if(args.length>1){
				String playerString = args[1];
				Player player = Bukkit.getPlayer(playerString);
				if(player==null){
					player = Bukkit.getPlayer(UUID.fromString(playerString));
				}
				if(player==null){
					sender.sendMessage("[§1ResourcepackManager§r] §cSpieler "+playerString+" nicht gefunden.");
					return true;
				}
				ResourcepackManager.playerMap.remove(player);
				ResourcepackManager.updateResourcepack(player);
				sender.sendMessage("[§1ResourcepackManager§r] §aResourcepack von "+player.getName()+" aktualisiert.");
			}
			else{
				for(Player player : Bukkit.getOnlinePlayers()){
					ResourcepackManager.playerMap.remove(player);
					ResourcepackManager.updateResourcepack(player);
				}
				sender.sendMessage("[§1ResourcepackManager§r] §aResourcepacks für alle Spieler aktualisiert.");
			}
			break;
		}
		}
		return true;
	}

}

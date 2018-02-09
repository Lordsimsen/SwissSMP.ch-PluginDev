package ch.swisssmp.playerstatues;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args==null||args.length==0) return false;
		if(!(sender instanceof Player)){
			sender.sendMessage("[PlayerStatues] Can only be used within the game.");
			return true;
		}
		Player player = (Player) sender;
		switch(args[0]){
		case "spawn":
		case "create":{
			if(args.length<2) return false;
			String playerName = args[1];
			String statueName;
			if(args.length>2) statueName = args[2];
			else statueName = "";
			if(PlayerStatueManager.create(player.getLocation(), playerName, statueName)){
				sender.sendMessage("[PlayerStatues] "+ChatColor.GREEN+"Figur platziert.");
			}
			else{
				sender.sendMessage("[PlayerStatues] "+ChatColor.RED+"Figur konnte nicht platziert werden.");
			}
			break;
		}
		case "update":{
			if(args.length<2) return false;
			String playerName = args[1];
			int range;
			if(args.length>2){
				try{
					range = Integer.parseInt(args[2]);
				}
				catch(Exception e){
					return false;
				}
			}
			else{
				range = 5;
			}
			if(PlayerStatueManager.update(player.getLocation(), playerName, range)){
				sender.sendMessage("[PlayerStatues] "+ChatColor.GREEN+"Figur geändert.");
			}
			else{
				sender.sendMessage("[PlayerStatues] "+ChatColor.RED+"Figur konnte nicht geändert werden.");
			}
			break;
		}
		case "destroy":
		case "delete":
		case "remove":{
			if(args.length<2) return false;
			String playerName = args[1];
			int range;
			if(args.length>2){
				try{
					range = Integer.parseInt(args[2]);
				}
				catch(Exception e){
					return false;
				}
			}
			else{
				range = 5;
			}
			if(PlayerStatueManager.remove(player.getLocation(), playerName, range)){
				sender.sendMessage("[PlayerStatues] "+ChatColor.GREEN+"Figur entfernt.");
			}
			else{
				sender.sendMessage("[PlayerStatues] "+ChatColor.RED+"Figur konnte nicht entfernt werden.");
			}
			break;
		}
		}
		return true;
	}
}

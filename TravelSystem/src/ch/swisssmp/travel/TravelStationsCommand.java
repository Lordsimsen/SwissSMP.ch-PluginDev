package ch.swisssmp.travel;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TravelStationsCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("Can only be used from within the game.");
			return true;
		}
		Player player = (Player) sender;
		
		boolean showAll = true;
		for(String flag : args){
			switch(flag.toLowerCase()){
			case "-welt":
			case "-world": showAll = false; break;
			default: player.sendMessage(TravelSystem.getPrefix()+"Unbekannte Flag "+flag);
			}
		}
		
		TravelStationsEditor.open(player, showAll);
		return true;
	}
}

package ch.swisssmp.travel;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TravelStationCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		
		switch(args[0]){
		case "erstelle":
		case "create":{
			if(!(sender instanceof Player)){
				sender.sendMessage("Can only be used from within the game.");
				return true;
			}
			if(args.length<2) return false;
			Player player = (Player) sender;
			
			String name = args[1];
			TravelStation station = TravelStation.create(player.getWorld(), name);
			if(station==null){
				player.sendMessage(TravelSystem.getPrefix()+ChatColor.RED+"Die Station konnte nicht erstellt werden.");
			}
			station.openEditor(player);
			player.getInventory().addItem(station.getTokenStack());
			
			return true;
		}
		default: return true;
		}
	}
}

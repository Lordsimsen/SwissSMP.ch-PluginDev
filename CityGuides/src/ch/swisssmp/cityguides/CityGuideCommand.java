package ch.swisssmp.cityguides;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CityGuideCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		
		switch(args[0]){
		case "create":{
			return true;
		}
		default: return false;
		}
	}
}

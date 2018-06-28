package ch.swisssmp.streets;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null||args.length==0) return false;
		switch(args[0]){
		case "reload":{
			Street.loadStreets();
			sender.sendMessage("[Strassen] Neu geladen.");
			break;
		}
		}
		return true;
	}

}

package ch.swisssmp.taxcollector;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ConsoleCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null) return false;
		if(args.length<1) return false;
		switch(args[0]){
			case "reload":{
				TaxCollector.reloadChests();
				break;
			}
			case "info":{
				if(args.length<2){
					sender.sendMessage("/tax info [player]");
					return true;
				}
				TaxCollector.inform_player(new String[]{
						"player="+args[1],
						"flags[]="+"other"
				}, sender);
				break;
			}
			default: 
				return false;
		}
		return true;
	}

}

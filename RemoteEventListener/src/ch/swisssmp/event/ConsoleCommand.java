package ch.swisssmp.event;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ConsoleCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "reload":{
			if(RemoteEventListener.loadEventListeners()){
				sender.sendMessage("[RemoteEventListener] EventListeners neu geladen.");
			}
			else{
				sender.sendMessage("[RemoteEventListener] Fehler beim laden der EventListeners.");
			}
			break;
		}
		default: return false;
		}
		return true;
	}
	
}
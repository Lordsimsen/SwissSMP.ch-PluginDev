package ch.swisssmp.event;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ch.swisssmp.event.pluginlisteners.EventListenerMaster;
import ch.swisssmp.event.remotelisteners.BasicEventListener;

public class ConsoleCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
			case "reload":{
				EventListenerMaster.getInst().loadEventListeners((success)->{
					if(success){
						sender.sendMessage("[RemoteEventListener] EventListeners neu geladen.");
					}
					else{
						sender.sendMessage("[RemoteEventListener] Fehler beim laden der EventListeners.");
					}
				});
				break;
			}
			case "debug":{
				EventListenerMaster.getInst().toggleDebug();
				if(EventListenerMaster.getInst().debugOn()){
					sender.sendMessage("[RemoteEventListener] Der Debug-Modus ist nun aktiviert.");
				}
				else{
					sender.sendMessage("[RemoteEventListener] Der Debug-Modus ist nun deaktiviert.");
				}
				break;
			}
			case "list":{
				for(BasicEventListener eventListener : EventListenerMaster.getInst().getEventListeners()){
					sender.sendMessage("- ["+eventListener.getType()+"] "+eventListener.getName());
				}
				break;
			}
			default: return false;
		}
		return true;
	}
	
}

package ch.swisssmp.world;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PlayerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "reload":{
			WorldManager.plugin.loadWorlds();
			break;
		}
		case "load":{
			if(args.length<2) return false;
			if(WorldManager.plugin.loadWorld(args[1])==null){
				sender.sendMessage("[WorldManager] Konnte Welt "+args[1]+" nicht laden.");
			};
			break;
		}
		case "unload":{
			if(args.length<2) return false;
			boolean save = true;
			if(args.length>2){
				save = !args[2].equals("False");
			}
			if(!WorldManager.plugin.unloadWorld(args[1], save)){
				sender.sendMessage("[WorldManager] Konnte Welt "+args[1]+" nicht laden.");
			};
			break;
		}
		default: return false;
		}
		return true;
	}

}

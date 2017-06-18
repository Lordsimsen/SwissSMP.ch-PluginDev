package ch.swisssmp.server;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length<1){
			return false;
		}
		switch(args[0]){
		case "reload":{
			ServerManager.plugin.UpdatePluginInfos();
			break;
		}
		case "rename":{
			if(args.length<2){
				return false;
			}
			ServerManager.plugin.Rename(args[1]);
			break;
		}
		default:{
			return false;
		}
		}
		return true;
	}

}

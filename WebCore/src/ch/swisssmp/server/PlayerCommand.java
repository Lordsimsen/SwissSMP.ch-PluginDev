package ch.swisssmp.server;

import java.util.ArrayList;

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
			ServerManager.getInstance().updatePluginInfos();
			ServerManager.getInstance().reload();
			sender.sendMessage("[ServerManager] Server aktualisiert.");
			break;
		}
		case "rename":{
			if(args.length<2){
				return false;
			}
			ArrayList<String> nameParts = new ArrayList<String>();
			for(int i = 1; i < args.length; i++){
				nameParts.add(args[i]);
			}
			String name = String.join(" ", nameParts);
			ServerManager.getInstance().rename(name);
			sender.sendMessage("[ServerManager] Der Server heisst nun '"+name+"'.");
			break;
		}
		default:{
			return false;
		}
		}
		return true;
	}

}

package ch.swisssmp.craftcity;

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
			CraftCity.unregisterRecipes();
			CraftCity.registerRecipes();
			break;
		}
		default: break;
		}
		return true;
	}

}

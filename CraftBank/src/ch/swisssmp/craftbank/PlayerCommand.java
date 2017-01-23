package ch.swisssmp.craftbank;

import ch.swisssmp.craftbank.Main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PlayerCommand implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(args==null) return false;
    	if(args.length<1) return false;
    	switch(args[0]){
	    	case "reload":
	    		Main.loadYamls();
	    		sender.sendMessage("[CraftBank] Konfiguraton neu geladen.");
				break;
	    	case "debug":{
	    		Main.debug = !Main.debug;
	    		break;
	    	}
		}
		return true;
	}
}

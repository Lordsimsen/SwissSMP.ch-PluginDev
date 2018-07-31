package ch.swisssmp.adventuredungeons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.adventuredungeons.AdventureDungeons;

public class AdventureDungeonsCommand implements CommandExecutor{
	Player player;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(sender instanceof Player)
    		player = (Player) sender;
    	else{
    		AdventureDungeons.info("Can only be executed from within the game.");
    		return true;
    	}
    	if(args.length==0){
    		return false;
    	}
    	switch(label){
    	case "AdventureDungeons":
    	case "mmo":
	    	switch(args[0]){
		    	case "help":
		    		return false;
		    	case "debug":
		    		AdventureDungeons.debug = !AdventureDungeons.debug;
		    		if(AdventureDungeons.debug){
		    			player.sendMessage(ChatColor.GREEN+"Der Debug-Modus ist nun eingeschaltet.");
		    		}
		    		else {
		    			player.sendMessage(ChatColor.RED+"Der Debug-Modus ist nun ausgeschaltet.");
		    		}
		    		break;
			}
	    	break;
    	}
		return true;
	}
}

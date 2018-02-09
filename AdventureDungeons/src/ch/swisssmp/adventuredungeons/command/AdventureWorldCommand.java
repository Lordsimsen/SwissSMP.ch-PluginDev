package ch.swisssmp.adventuredungeons.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.world.AdventureWorld;

public class AdventureWorldCommand implements CommandExecutor{
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
    		displayHelp();
    		return true;
    	}
    	switch(args[0]){
	    	case "help":
	    		displayHelp();
	    		break;
	    	case "reload":
				try {
					boolean fullload = false;
					if(args.length>1){
						fullload = (args[1].equals("all"));
					}
					AdventureWorld.loadWorlds(fullload);
		    		player.sendMessage("[CraftMMO] Welten-Konfiguration neu geladen.");
				} catch (Exception e) {
					player.sendMessage("Fehler beim laden der Daten! Mehr Details in der Konsole...");
					e.printStackTrace();
				}
				break;
		}
		return true;
	}
    public void displayHelp(){
    	player.sendMessage("/MmoWorld = /mmoworld");
    	player.sendMessage("-----");
    	player.sendMessage("/mmoworld help - Zeigt diese Hilfe an");
    	player.sendMessage("/mmoworld create [name] - Generiert eine MMO-Welt");
    }
}
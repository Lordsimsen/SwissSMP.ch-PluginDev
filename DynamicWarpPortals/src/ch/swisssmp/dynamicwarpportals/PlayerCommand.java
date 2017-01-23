package ch.swisssmp.dynamicwarpportals;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PlayerCommand implements CommandExecutor{
	private CommandSender _sender;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	_sender = sender;
    	switch(args[0]){
	    	case "help":
	    		displayHelp();
	    		break;
	    	case "dest":
	    	case "destination":
	    		if(args.length<3){
	    			displayHelp();
	    			break;
	    		}
	    		String action = args[1].toLowerCase();
	    		if(!action.equals("set") && !action.equals("remove")){
	    			displayHelp();
	    			break;
	    		}
	    		String name = args[2];
	        	if(!(sender instanceof Player)){
	        		sendMessage("Kann nur von einem Spieler ausgeführt werden");
	        		break;
	        	}
	    		Player player = (Player) sender;
	        	if(action.equals("set")){
		    		World world = player.getWorld();
		    		Vector vector = player.getLocation().toVector();
		    		ConfigurationSection locationSection = Main.destinations.createSection(name);
		    		locationSection.set("world", world.getName());
		    		locationSection.set("vector", vector);
			    	Main.saveYamls();
			    	player.sendMessage(ChatColor.GREEN+"Destination "+name+" gespeichert.");
	        	}
	        	else if(action.equals("remove")){
		    		Main.destinations.set(name, null);
			    	Main.saveYamls();
			    	player.sendMessage(ChatColor.GREEN+"Destination "+name+" gelöscht.");
	        	}
	    		break;
	    	case "reload":
	    		Main.loadYamls();
				break;
		}
		return true;
	}
    public void displayHelp(){
		sendMessage("DynamicWarpPortals Version "+Main.pdfFile.getVersion()+" Befehle:");
		sendMessage("/DynamicWarpPortals = /dwp");
		sendMessage("-----");
		sendMessage("/dwp help - Zeigt diese Hilfe an");
		sendMessage("/dwp destination [set/remove] [name] Setzt/Löscht einen Teleportpunkt");
		sendMessage("/dwp reload - Lädt die Zieldestinationen neu");
    }
    private void sendMessage(String message){
		_sender.sendMessage(message);
    }
}

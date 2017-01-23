package ch.swisssmp.bigdoors;

import ch.swisssmp.bigdoors.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


public class PlayerCommand implements CommandExecutor, TabCompleter{
	Player player;
	List<String> commands = Arrays.asList("help", "reload", "set", "remove", "open", "close", "openmessage", "closemessage");
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(sender instanceof Player)
    		player = (Player) sender;
    	else{
    		Main.logger.info("Can only be executed from within the game.");
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
	    		Main.loadYamls();
	    		player.sendMessage("[BigDoors] Konfiguraton neu geladen.");
				break;
	    	case "set":
	    		if(args.length<3){
	    			displayHelp();
	    			break;
	    		}
	    		String door = args[1];
	    		ConfigurationSection doorSection;
	    		if(!Main.doors.contains("doors."+door)){
		    		doorSection = Main.doors.createSection("doors."+door);
	    		}
	    		else doorSection = Main.doors.getConfigurationSection("doors."+door);
	    		String state = args[2];
	    		String statename;
	    		if(state.equals("0"))
	    			statename = "Geschlossen";
	    		else if(state.equals("1"))
	    			statename = "Offen";
	    		else{
	    			player.sendMessage(ChatColor.RED+"Als Zustand sind nur 0 oder 1 erlaubt. (0=Zu, 1=Offen)");
	    			break;
	    		}
	    		String schematicName = door+"_"+state+".schematic";
	    		Vector pos = Schematic.save(player, schematicName);
	    		if(pos!=null){
	    			ConfigurationSection stateSection = doorSection.createSection(state);
	    			stateSection.set("schematic", schematicName);
	    			stateSection.set("vector", pos);
	    			stateSection.set("world", player.getWorld().getName());
		    		player.sendMessage(ChatColor.GREEN+"'"+door+"' (Zustand "+statename+") gespeichert!");
	    		}
	    		Main.saveYamls();
	    		break;
	    	case "open":
	    		if(args.length<2){
	    			displayHelp();
	    			break;
	    		}
	    		door = args[1];
	    	case "close":
	    		if(args.length<2){
	    			displayHelp();
	    			break;
	    		}
	    		door = args[1];
	    		if(args[0].equals("open")){
		    		if(!Door.open(door, player))
		    			player.sendMessage(ChatColor.RED+"'Konnte "+door+"' nicht öffnen.");
	    		}
	    		else{
		    		if(!Door.close(door, player))
		    			player.sendMessage(ChatColor.RED+"'Konnte "+door+"' nicht schliessen.");
	    		}
	    		break;
	    	case "remove":
	    		if(args.length<2){
	    			displayHelp();
	    			break;
	    		}
	    		door = args[1];
	    		Main.doors.set("doors."+door, null);
	    		for(String handler : Main.doors.getConfigurationSection("handlers").getKeys(false)){
	    			if(Main.doors.getString("handlers."+handler).equals(door))
	    			{
	    				Main.doors.set("handlers."+handler, null);
	    			}
	    		}
	    		player.sendMessage(ChatColor.RED+"'"+door+"' gelöscht.");
	    		Main.saveYamls();
	    		break;
	    	case "openmessage":
	    	case "closemessage":
	    		if(args.length<2){
	    			displayHelp();
	    			break;
	    		}
	    		door = args[1];
	    		if(!Main.doors.contains("doors."+door))
	    			break;
	    		state = "1";
	    		if(args[0].equals("closemessage"))
	    			state="0";
	    		String message = null;
	    		if(args.length>=3){
	    			message = "";
	    			for(int i = 2; i < args.length; i++){
	    				message += args[i]+" ";
	    			}
	    			message = message.trim();
	    		}
	    		Main.doors.set("doors."+door+"."+state+".message", message);
	    		if(message!=null){
		    		player.sendMessage("Nachricht gesetzt auf '"+message+"'!");
	    		}
	    		else
	    			player.sendMessage("Nachricht entfernt!");
	    		Main.saveYamls();
	    		break;
    		default:
    			displayHelp();
    			break;
		}
		return true;
	}
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
    	if(sender instanceof Player)
    		player = (Player) sender;
    	else{
    		Main.logger.info("Can only be executed from within the game.");
    		return null;
    	}
    	if(args.length<=1){
    		return commands;
    	}
    	switch(args[0]){
	    	case "help":
	    	case "reload":
	    		return null;
	    	case "set":
	    		if(args.length>2){
	    			return Arrays.asList("0", "1");
	    		}
	    	case "open":
	    	case "close":
	    	case "remove":
	    	case "openmessage":
	    	case "closemessage":
	    		if(args.length>2 || !Main.doors.contains("doors")){
	    			return null;
	    		}
	    		List<String> result = new ArrayList<String>();
	    		Set<String> candidates = Main.doors.getConfigurationSection("doors").getKeys(false);
	    		if(args.length>1){
		    		for(String candidate : candidates){
		    			if(candidate.toLowerCase().startsWith(args[1].toLowerCase()))
		    					result.add(candidate);
		    		}
	    		}
	    		else result.addAll(candidates);
	    		return result;
    		default:
    			return null;
		}
    }
    public void displayHelp(){
    	player.sendMessage("BigDoors Version "+Main.pdfFile.getVersion()+" Befehle:");
    	player.sendMessage("/BigDoors = /door");
    	player.sendMessage("-----");
    	player.sendMessage("/door help - Zeigt diese Hilfe an");
    	player.sendMessage("/door reload - Lädt die Konfigurationen neu");
    	player.sendMessage("/door set [name] [Zustand (0 oder 1)] - Erstellt eine neue Tür");
    	player.sendMessage("/door remove [name] - Entfernt eine Tür");
    	player.sendMessage("/door open [name] - Öffnet eine Tür");
    	player.sendMessage("/door openmessage [name] [message] - Setzt Nachricht beim Öffnen");
    	player.sendMessage("/door close [name] - Schliesst eine Tür");
    	player.sendMessage("/door closemessage [name] [message] - Setzt Nachricht beim Schliessen");
    }
}

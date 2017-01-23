/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.swisssmp.spleef;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.webcore.DataSource;

/**
 *
 * @author cagat
 */
public class PlayerCommand implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if(args == null) return false;
        if(args.length == 0) return false;
        
        switch(args[0])
        {
            case "reload":
                Arena.loadArenas();
                sender.sendMessage("Spleef Arenen neu geladen");
                break;
                
            case "debug":
                Spleef.debug = !Spleef.debug;
                if(Spleef.debug)
                        sender.sendMessage("Der Debug-Modus ist nun aktiviert.");
                else
                    sender.sendMessage("Der Debug-Modus ist nun deaktiviert.");
                
                break;
            case "reset":
            {
            	if(args.length<2){
            		sender.sendMessage("Bitte eine ID angeben.");
            		return true;
            	}
            	int arena_id = Integer.parseInt(args[1]);
            	Arena arena = Arena.get(arena_id);
            	if(arena==null){
            		sender.sendMessage("Arena "+args[1]+" nicht gefunden.");
            		return true;
            	}
            	arena.resetGame();
            	sender.sendMessage("Arena "+arena_id+" zurückgesetzt.");
            	break;
            }
            	
            case "save":
            {
            	if(!(sender instanceof Player))
            	{
            		sender.sendMessage("Can only be executed from within the game");
            		return true;
            	}
            	
            	Player player = (Player) sender;
            	if(args.length<2){
            		sender.sendMessage("Bitte eine ID angeben.");
            		return true;
            	}
            	int arena_id = Integer.parseInt(args[1]);
            	String schematicName = "arena_" + arena_id;
            	Arena arena = Arena.get(arena_id);
            	if(arena==null){
            		sender.sendMessage("Arena "+args[1]+" nicht gefunden.");
            		return true;
            	}
            	Location saveLocation = SchematicUtil.save(player, schematicName);
            	DataSource.getResponse("spleef/schematic_location.php", new String[]{
            			"world="+saveLocation.getWorld().getName(),
            			"x="+saveLocation.getX(),
            			"y="+saveLocation.getY(),
            			"z="+saveLocation.getZ(),
            			"arena="+arena_id
            	});
            	sender.sendMessage("Arena "+arena_id+" gespeichert.");
            	break;
            }
                
            default:
                break;
        }
        
        return true;
    }
    
}

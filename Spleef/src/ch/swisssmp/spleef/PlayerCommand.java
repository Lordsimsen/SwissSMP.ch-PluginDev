/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.swisssmp.spleef;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
                
            default:
                break;
        }
        
        return true;
    }
    
}

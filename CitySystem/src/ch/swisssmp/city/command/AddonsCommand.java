package ch.swisssmp.city.command;

import ch.swisssmp.city.CitySystem;
import ch.swisssmp.city.CitySystemPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AddonsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<1){
            return false;
        }
        switch(args[0]){
            case "reload":{
                CitySystem.reloadAddons((success)->{
                    if(success){
                        sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.GREEN+" Alle Addons aktualisiert.");
                    }
                    else{
                        sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.RED+" Addons konnten nicht aktualisiert werden.");
                    }
                });
                return true;
            }
            default: return false;
        }
    }
}

package ch.swisssmp.city.commands;

import ch.swisssmp.city.CitySystem;
import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.TechtreesView;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TechtreesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<1){
            if(!(sender instanceof Player)){
                sender.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED);
            }
            TechtreesView.open((Player) sender);
            return true;
        }
        switch(args[0]){
            case "reload":{
                CitySystem.reloadTechtrees((success)->{
                    if(success){
                        sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.GREEN+" Alle Techtrees aktualisiert.");
                    }
                    else{
                        sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.RED+" Techtrees konnten nicht aktualisiert werden.");
                    }
                });
                return true;
            }
            default: return false;
        }
    }
}

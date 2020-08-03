package ch.swisssmp.city.commands;

import ch.swisssmp.city.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CitiesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<1){
            if (!(sender instanceof Player)) {
                sender.sendMessage(CitySystemPlugin.getPrefix() + " Kann nur ingame verwendet werden.");
                return true;
            }

            Player player = (Player) sender;
            CitiesView.open(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload": {
                CitySystem.reloadCities((success)->{
                    if(success){
                        sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GREEN + " Städte neu geladen.");
                    }
                    else{
                        sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.RED + " Konnte Städte nicht neu laden.");
                    }
                });
                return true;
            }
            default:
                return false;
        }
    }

}

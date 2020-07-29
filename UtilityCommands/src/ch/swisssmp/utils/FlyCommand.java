package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if(args.length>0){
            player = Bukkit.getPlayer(args[0]);
        }
        else if(sender instanceof Player){
            player = (Player) sender;
        }
        else{
            sender.sendMessage("/fly kann nur ingame verwendet werden.");
            return true;
        }
        if(player==null){
            sender.sendMessage(args[0]+" nicht gefunden.");
            return true;
        }
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.sendMessage(ChatColor.AQUA + "Flugmodus deaktiviert.");
        } else {
            player.setAllowFlight(true);
            player.sendMessage(ChatColor.AQUA + "Flugmodus aktiviert.");
        }
        if(!player.equals(sender)) {
            sender.sendMessage(ChatColor.AQUA + "Flugmodus für " + ChatColor.DARK_PURPLE + player.getName()
                    + ChatColor.AQUA + (player.getAllowFlight() ? " aktiviert." : " deaktiviert.");
        }
        
        return true;
    }
}

package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if(args.length>0){
            player = Bukkit.getPlayer(args[0]);
        } else if(sender instanceof Player){
            player = (Player) sender;
        } else{
            sender.sendMessage("/top kann nur ingame verwendet werden.");
            return true;
        }
        if(player == null){
            sender.sendMessage(args[0] + " nicht gefunden.");
            return true;
        }
        Location playerLocation = player.getLocation();
        Location topLocation = playerLocation.add(0,playerLocation.getWorld().getHighestBlockYAt(playerLocation),0);
        player.teleport(topLocation);
        return true;
    }
}

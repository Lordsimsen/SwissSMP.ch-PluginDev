package ch.swisssmp.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlySpeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if(args.length != 1){
            return false;
        }
        if(sender instanceof Player){
            player = (Player) sender;
        }
        else{
            sender.sendMessage("/fly kann nur ingame verwendet werden.");
            return true;
        }
        float speed;
        if(args[0].equalsIgnoreCase("default")){
            speed = 0.1f;
        } else{
            speed = Float.parseFloat(args[0]);
            if(speed > 1.0f){
                player.sendMessage(ChatColor.RED + "Maximale Fluggeschwindigkeit ist 1.0!");
                return true;
            }
        }
        player.setFlySpeed(speed);

        player.sendMessage(ChatColor.AQUA + "Fluggeschwindigkeit ist nun " + speed);
        return true;
    }
}

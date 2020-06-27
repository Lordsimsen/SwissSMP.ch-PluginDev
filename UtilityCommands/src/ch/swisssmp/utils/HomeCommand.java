package ch.swisssmp.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if(args.length>0){
            if(!args[0].equalsIgnoreCase("set")) return false;
            if(!(sender instanceof Player)){
                sender.sendMessage("/home set kann nur ingame verwendet werden");
                return true;
            }
            player = (Player) sender;
            Location home = player.getLocation();
            player.setBedSpawnLocation(home, true);
            player.sendMessage(ChatColor.GREEN + " Spawnpunkt gesetzt.");
            return true;
        }
        if(!(sender instanceof Player)){
            sender.sendMessage("/home kann nur ingame verwendet werden");
            return true;
        }
        player = (Player) sender;
        try {
            player.teleport(player.getBedSpawnLocation());
        } catch (Exception e){
            player.sendMessage(ChatColor.RED + "Dein Bett ist blockiert..");
        }
        return true;
    }
}

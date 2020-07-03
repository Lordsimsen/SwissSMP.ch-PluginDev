package ch.swisssmp.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BackCommand implements CommandExecutor {

    private static HashMap<UUID,Location> backLocations = new HashMap<>();

    public static Location getBackLocation(Player player){
        if(backLocations.containsKey(player.getUniqueId())){
            return backLocations.get(player.getUniqueId());
        } else{
            return null;
        }
    }

    public static void setBackLocation(Player player, Location location){
        backLocations.put(player.getUniqueId(), location);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("/back kann nur ingame verwendet werden");
            return true;
        }
        Player player = (Player) sender;
        Location location = getBackLocation(player);
        if(location == null){
            player.sendMessage(ChatColor.RED + "Es gibt keinen Punkt an den du zurückkehren könntest.");
            return true;
        }
        World world = location.getWorld();
        if(world != null) {
            player.teleport(location);
            return true;
        } else{
            player.sendMessage(ChatColor.RED + "Welt existiert nicht mehr..");
            backLocations.remove(player.getUniqueId());
            return true;
        }
    }
}
